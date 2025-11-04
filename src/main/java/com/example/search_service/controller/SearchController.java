package com.example.search_service.controller;

import com.example.search_service.dto.SearchResultDTO;
import com.example.search_service.models.ProductDocument;
import com.example.search_service.repositories.ProductRepository;
import com.example.search_service.services.ProductSearchServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/search")
public class SearchController {

    private final ProductRepository repository;
    private final ProductSearchServiceImpl productSearchService;

    @GetMapping("/test")
    public ResponseEntity<?> searchProducts(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) List<String> categories,
            @RequestParam(required = false) List<String> characters,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id") String sortBy, //tìm kiếm theo các field id,name.keyword,createdAt,status
            @RequestParam(defaultValue = "desc") String sortOrder //asc, desc
    ) {
        try {
            SearchResultDTO result = productSearchService.searchProducts(
                    q,
                    categories,
                    characters,
                    minPrice,
                    maxPrice,
                    status,
                    page,
                    limit,
                    sortBy,
                    sortOrder
            );

            return ResponseEntity.ok(result);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi trong quá trình tìm kiếm: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Yêu cầu không hợp lệ: " + e.getMessage());
        }
    }
}

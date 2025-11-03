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

    @GetMapping
    public List<ProductDocument> search(@RequestParam String q) {
        return repository.findByNameContainingOrDescriptionContaining(q, q);
    }

    @GetMapping("/test")
    public ResponseEntity<?> searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) List<String> categories,
            @RequestParam(required = false) List<String> characters,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id") String sortBy,     // Service có fallback, nhưng đặt default ở đây rõ ràng hơn
            @RequestParam(defaultValue = "desc") String sortOrder // Service có fallback, nhưng đặt default ở đây rõ ràng hơn
    ) {
        try {
            // Gọi service với các tham số đã nhận
            SearchResultDTO result = productSearchService.searchProducts(
                    name,
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

            // Trả về kết quả tìm kiếm thành công (HTTP 200 OK)
            return ResponseEntity.ok(result);

        } catch (IOException e) {
            // Xử lý lỗi nếu có sự cố khi giao tiếp với Elasticsearch
            // Trả về lỗi 500 Internal Server Error
            e.printStackTrace(); // Nên log lỗi này trong thực tế
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi trong quá trình tìm kiếm: " + e.getMessage());
        } catch (Exception e) {
            // Xử lý các lỗi ngoại lệ khác (ví dụ: tham số không hợp lệ không được Spring xử lý)
            // Trả về lỗi 400 Bad Request
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Yêu cầu không hợp lệ: " + e.getMessage());
        }
    }
}

package com.example.search_service.controller;

import com.example.search_service.models.ProductDocument;
import com.example.search_service.repositories.ProductRepository;
import com.example.search_service.services.ProductSearchService;
import lombok.RequiredArgsConstructor;
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
    private final ProductSearchService productSearchService;

    @GetMapping
    public List<ProductDocument> search(@RequestParam String q) {
        return repository.findByNameContainingOrDescriptionContaining(q, q);
    }

    @GetMapping("/fuzzy")
    public ProductSearchService.SearchResult searchFuzzyByName(@RequestParam String q) throws IOException {
        return productSearchService.searchFuzzyByName(q);
    }

    @GetMapping("/all")
    public ProductSearchService.SearchResult getAllProducts() throws IOException {
        return productSearchService.getAllProducts();
    }
}

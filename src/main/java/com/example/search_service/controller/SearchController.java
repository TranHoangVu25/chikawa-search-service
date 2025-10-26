package com.example.search_service.controller;

import com.example.search_service.models.ProductDocument;
import com.example.search_service.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {

    private final ProductRepository repository;

    @GetMapping
    public List<ProductDocument> search(@RequestParam String q) {
        return repository.findByNameContainingOrDescriptionContaining(q, q);
    }
}

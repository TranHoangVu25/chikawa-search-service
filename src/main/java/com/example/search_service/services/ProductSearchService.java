package com.example.search_service.services;

import com.example.search_service.dto.SearchResultDTO;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public interface ProductSearchService {
//     SearchResultDTO1 searchFuzzyByName(String keyword) throws IOException;

    SearchResultDTO searchProducts(
            String name,
            List<String> categorySlugs,
            List<String> characterSlugs,
            Double minPrice,
            Double maxPrice,
            String status,
            int page,
            int limit,
            String sortBy,
            String sortOrder
    ) throws IOException;
}

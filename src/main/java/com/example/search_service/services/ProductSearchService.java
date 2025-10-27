package com.example.search_service.services;

import com.example.search_service.models.ProductDocument;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public interface ProductSearchService {
     ProductSearchServiceImpl.SearchResult searchFuzzyByName(String keyword) throws IOException;

     record SearchResult(long count, List<ProductDocument> results){}

     ProductSearchServiceImpl.SearchResult getAllProducts() throws IOException;
}

package com.example.search_service.services;

import com.example.search_service.dto.SearchResultDTO;
import com.example.search_service.models.ProductDocument;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public interface ProductSearchService {
     SearchResultDTO searchFuzzyByName(String keyword) throws IOException;

     SearchResultDTO getAllProducts() throws IOException;

     SearchResultDTO searchFuzzyByCategory(String keyword) throws IOException;

     SearchResultDTO searchFuzzyByCharacter(String keyword) throws IOException;

     }

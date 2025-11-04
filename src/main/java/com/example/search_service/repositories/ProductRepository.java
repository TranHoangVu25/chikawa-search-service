package com.example.search_service.repositories;

import com.example.search_service.models.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends ElasticsearchRepository<ProductDocument, String> { }


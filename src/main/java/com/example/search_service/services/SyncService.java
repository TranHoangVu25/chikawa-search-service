package com.example.search_service.services;

import com.example.search_service.models.ProductDocument;
import com.example.search_service.models.ProductSearchEvent;
import com.example.search_service.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SyncService {

    private final ProductRepository elasticRepo;
    private final RestTemplate restTemplate;

    @Value("${product.service.url}")
    private String productServiceUrl; // http://product-service:8082/api/products/all

    public void syncAllProducts() {
        ProductSearchEvent[] products = restTemplate.getForObject(productServiceUrl, ProductSearchEvent[].class);
        if (products == null || products.length == 0) {
            System.out.println("⚠️ Không có sản phẩm nào để đồng bộ.");
            return;
        }

        List<ProductDocument> docs = Arrays.stream(products)
                .map(p -> ProductDocument.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .description(p.getDescription())
                        .price(p.getPrice())
                        .categories(p.getCategories() != null ? p.getCategories() : List.of())
                        .characters(p.getCharacters() != null ? p.getCharacters() : List.of())
                        .build())
                .toList();
        elasticRepo.saveAll(docs);
        System.out.println("✅ Đã đồng bộ " + docs.size() + " sản phẩm vào Elasticsearch.");
    }
}

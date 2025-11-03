package com.example.search_service.services;

import com.example.search_service.dto.request.CategoryDTO;
import com.example.search_service.dto.request.CharacterDTO;
import com.example.search_service.dto.response.ApiResponse;
import com.example.search_service.models.ProductDocument;
import com.example.search_service.models.ProductSearchEvent;
import com.example.search_service.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
        ResponseEntity<ApiResponse<ProductSearchEvent[]>> response = restTemplate.exchange(
                productServiceUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<ApiResponse<ProductSearchEvent[]>>() {}
        );

        ProductSearchEvent[] products = response.getBody().getResult();
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
                        .status(p.getStatus())
                        .categories(
                                p.getCategories() != null
                                        ? p.getCategories().stream()
                                        .map(CategoryDTO::getName) // hoặc getSlug()
                                        .toList()
                                        : List.of()
                        )
                        .characters(
                                p.getCharacters() != null
                                        ? p.getCharacters().stream()
                                        .map(CharacterDTO::getName)
                                        .toList()
                                        : List.of()
                        )
                        .build())
                .toList();

        elasticRepo.saveAll(docs);
        System.out.println(docs);
        System.out.println("✅ Đã đồng bộ " + docs.size() + " sản phẩm vào Elasticsearch.");
    }

}

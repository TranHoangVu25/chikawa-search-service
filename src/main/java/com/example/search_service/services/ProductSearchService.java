package com.example.search_service.services;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.example.search_service.models.ProductDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final ElasticsearchClient elasticClient;

    public SearchResult searchFuzzyByName(String keyword) throws IOException {
        SearchResponse<ProductDocument> response = elasticClient.search(s -> s
                        .index("product_service")
                        .query(q -> q
                                .fuzzy(f -> f
                                        .field("name")
                                        .value(keyword)
                                        .fuzziness("AUTO")
                                )
                        ),
                ProductDocument.class
        );

        List<ProductDocument> results = response.hits().hits().stream()
                .map(Hit::source)
                .toList();

        long count = response.hits().total() != null ? response.hits().total().value() : results.size();

        return new SearchResult(count, results);
    }

    // DTO trả về kết quả + số lượng (field name = count)
    public record SearchResult(long count, List<ProductDocument> results) {}

    public SearchResult getAllProducts() throws IOException {
        SearchResponse<ProductDocument> response = elasticClient.search(s -> s
                        .index("product_service")
                        .query(q -> q.matchAll(m -> m))
                        .size(10000),  // lấy tối đa 10k bản ghi, nếu nhiều hơn thì dùng scroll API
                ProductDocument.class
        );

        List<ProductDocument> results = response.hits().hits().stream()
                .map(Hit::source)
                .toList();

        long count = response.hits().total() != null ? response.hits().total().value() : results.size();

        return new SearchResult(count, results);
    }

}

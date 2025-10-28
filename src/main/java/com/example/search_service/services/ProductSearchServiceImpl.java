package com.example.search_service.services;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.example.search_service.dto.SearchResultDTO;
import com.example.search_service.models.ProductDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSearchServiceImpl implements ProductSearchService{
    private final ElasticsearchClient elasticClient;

    @Override
    public SearchResultDTO searchFuzzyByName(String keyword) throws IOException {
        SearchResponse<ProductDocument> response = elasticClient.search(s -> s
                        .index("product_service")
                        .query(q -> q
                                //match giúp nối chuỗi trong keyword để tìm kiếm
                                .match(m -> m
                                                .field("name")
                                                .query(keyword)
                                                .fuzziness("2")
                                                .operator(Operator.And)
                                )
                        ),
                ProductDocument.class
        );

        List<ProductDocument> results = response.hits().hits().stream()
                .map(Hit::source)
                .toList();

        long count = response.hits().total() != null ? response.hits().total().value() : results.size();

        return new SearchResultDTO(count, results);
    }

    public SearchResultDTO searchFuzzyByCategory(String keyword) throws IOException {
        SearchResponse<ProductDocument> response = elasticClient.search(s -> s
                        .index("product_service")
                        .query(q -> q
                                //match giúp nối chuỗi trong keyword để tìm kiếm
                                .match(m -> m
                                        .field("categories")
                                        .query(keyword)
                                        .fuzziness("2")
                                        .operator(Operator.And)
                                )
                        ),
                ProductDocument.class
        );

        List<ProductDocument> results = response.hits().hits().stream()
                .map(Hit::source)
                .toList();

        long count = response.hits().total() != null ? response.hits().total().value() : results.size();

        return new SearchResultDTO(count, results);
    }

    @Override
    public SearchResultDTO searchFuzzyByCharacter(String keyword) throws IOException {
        SearchResponse<ProductDocument> response = elasticClient.search(s -> s
                        .index("product_service")
                        .query(q -> q
                                //match giúp nối chuỗi trong keyword để tìm kiếm
                                .match(m -> m
                                        .field("characters")
                                        .query(keyword)
                                        .fuzziness("2")
                                        .operator(Operator.And)
                                )
                        ),
                ProductDocument.class
        );

        List<ProductDocument> results = response.hits().hits().stream()
                .map(Hit::source)
                .toList();

        long count = response.hits().total() != null ? response.hits().total().value() : results.size();

        return new SearchResultDTO(count, results);
    }
    @Override
    public SearchResultDTO getAllProducts() throws IOException {
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

        return new SearchResultDTO(count, results);
    }
}

package com.example.search_service.services;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.example.search_service.dto.CountItemDTO;
import com.example.search_service.dto.SearchResultDTO;
import com.example.search_service.models.ProductDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductSearchServiceImpl implements ProductSearchService {

    private final ElasticsearchClient elasticClient;

    @Override
    public SearchResultDTO searchProducts(
            String name,
            List<String> categories,
            List<String> characters,
            Double minPrice,
            Double maxPrice,
            String status,
            int page,
            int limit,
            String sortBy,
            String sortOrder
    ) throws IOException {

        int from = (page - 1) * limit;

        // Tạo truy vấn fuzzy cho name
        Query byNameQuery = null;
        if (name != null && !name.isEmpty()) {
            byNameQuery = MatchQuery.of(m -> m
                    .field("name")
                    .query(name)
                    .fuzziness("2")
            )._toQuery();
        }

        // Tạo danh sách filter
        List<Query> filters = new ArrayList<>();

        if (categories != null && !categories.isEmpty()) {
            filters.add(TermsQuery.of(t -> t
                    .field("categories.keyword")
                    .terms(v -> v.value(categories.stream().map(FieldValue::of).toList()))
            )._toQuery());
        }

        if (characters != null && !characters.isEmpty()) {
            filters.add(TermsQuery.of(t -> t
                    .field("characters.keyword")
                    .terms(v -> v.value(characters.stream().map(FieldValue::of).toList()))
            )._toQuery());
        }

        if (minPrice != null || maxPrice != null) {
            RangeQuery.Builder range = new RangeQuery.Builder().field("price");
            if (minPrice != null) range.gte(JsonData.of(minPrice));
            if (maxPrice != null) range.lte(JsonData.of(maxPrice));
            filters.add(range.build()._toQuery());
        }

        if (status != null && !status.isEmpty()) {
            // Sửa lại: Dùng TermQuery trên trường "status" (vì nó là keyword gốc)
            filters.add(TermQuery.of(t -> t
                    .field("status")
                    .value(status)
                    .caseInsensitive(true)
            )._toQuery());
        }

        // Kết hợp tất cả điều kiện
        BoolQuery.Builder boolBuilder = new BoolQuery.Builder();
        if (byNameQuery != null) boolBuilder.must(byNameQuery);
        if (!filters.isEmpty()) boolBuilder.filter(filters);

        // Sắp xếp
//        List<SortOptions> sortOptions = new ArrayList<>();
//        if (sortBy != null && !sortBy.isEmpty()) {
//            sortOptions.add(SortOptions.of(s -> s
//                    .field(f -> f
//                            .field(sortBy)
//                            .order("desc".equalsIgnoreCase(sortOrder) ? SortOrder.Desc : SortOrder.Asc)
//                    )
//            ));
//        } else {
//            sortOptions.add(SortOptions.of(s -> s
//                    .field(f -> f.field("id").order(SortOrder.Desc))
//            ));
//        }
// 🔽 Nếu có tìm theo name => ưu tiên sắp theo score
        List<SortOptions> sortOptions = new ArrayList<>();
        if (name != null && !name.isEmpty()) {
            sortOptions.add(SortOptions.of(s -> s
                    .score(sc -> sc.order(SortOrder.Desc))
            ));
        } else if (sortBy != null && !sortBy.isEmpty()) {
            sortOptions.add(SortOptions.of(s -> s
                    .field(f -> f
                            .field(sortBy)
                            .order("desc".equalsIgnoreCase(sortOrder) ? SortOrder.Desc : SortOrder.Asc)
                    )
            ));
        } else {
            sortOptions.add(SortOptions.of(s -> s
                    .field(f -> f.field("id").order(SortOrder.Desc))
            ));
        }

        // Gửi truy vấn Elasticsearch
        SearchResponse<ProductDocument> response = elasticClient.search(s -> s
                        .index("product_service")
                        .query(boolBuilder.build()._toQuery())
                        .from(from)
                        .size(limit)
                        .sort(sortOptions)
                        .aggregations("categories_count", a -> a.terms(t -> t.field("categories.keyword")))
                        .aggregations("characters_count", a -> a.terms(t -> t.field("characters.keyword")))
                        .aggregations("status_count", a -> a.terms(t -> t.field("status"))), // <--- SỬA LẠI 2: Bỏ .keyword
                ProductDocument.class
        );

        // Lấy kết quả
        List<ProductDocument> results = response.hits().hits().stream()
                .map(Hit::source)
                .filter(Objects::nonNull)
                .toList();

        long total = response.hits().total() != null
                ? response.hits().total().value()
                : results.size();

        // Xử lý Aggregation (nếu có)
        List<CountItemDTO> categoriesCount = new ArrayList<>();
        List<CountItemDTO> charactersCount = new ArrayList<>();
        List<CountItemDTO> statusCount = new ArrayList<>();

        if (response.aggregations().get("categories_count") != null) {
            categoriesCount = response.aggregations().get("categories_count").sterms().buckets().array().stream()
                    .map(b -> new CountItemDTO(b.key().stringValue(), b.docCount()))
                    .toList();
        }

        if (response.aggregations().get("characters_count") != null) {
            charactersCount = response.aggregations().get("characters_count").sterms().buckets().array().stream()
                    .map(b -> new CountItemDTO(b.key().stringValue(), b.docCount()))
                    .toList();
        }

        if (response.aggregations().get("status_count") != null) {
            statusCount = response.aggregations().get("status_count").sterms().buckets().array().stream()
                    .map(b -> new CountItemDTO(b.key().stringValue(), b.docCount()))
                    .toList();
        }
        return new SearchResultDTO(total, page, limit, status, results, charactersCount, categoriesCount, statusCount);
    }
}
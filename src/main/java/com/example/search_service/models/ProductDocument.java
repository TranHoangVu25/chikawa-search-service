package com.example.search_service.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

@Document(indexName = "product_service")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductDocument {
    @Id
    private String id;
    private String name;
    private Double price;
    private String status;
    private List<String> categories;
    private List<String> characters;
    private List<String> images;
}

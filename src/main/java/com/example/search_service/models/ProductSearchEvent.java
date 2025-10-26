package com.example.search_service.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSearchEvent {
    private String id;
    private String name;
    private String description;
    private Double price;
    private List<String> categories;
    private List<String> characters;
}

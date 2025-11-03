package com.example.search_service.models;

import com.example.search_service.dto.request.CategoryDTO;
import com.example.search_service.dto.request.CharacterDTO;
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
    private String status;
    private List<CategoryDTO> categories;
    private List<CharacterDTO> characters;
}

package com.example.search_service.models;

import com.example.search_service.Enums.Action;
import com.example.search_service.dto.request.CategoryDTO;
import com.example.search_service.dto.request.CharacterDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductReceiveEvent {
    private String id;
    private String name;
    private Double price;
    @JsonProperty("status")
    private String status;
    private List<CategoryDTO> categories;
    private List<CharacterDTO> characters;
    private List<String> images;
    private Action action;
}

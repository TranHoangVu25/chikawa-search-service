package com.example.search_service.models;

import com.example.search_service.dto.request.CategoryDTO;
import com.example.search_service.dto.request.CharacterDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSearchEvent {
    private String id;
    private String name;
    private List<CategoryDTO> categories;
    private List<CharacterDTO> characters;
    private Double price;
    private String status;
    private List<String> images;
}

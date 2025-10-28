package com.example.search_service.dto;

import com.example.search_service.models.ProductDocument;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class SearchResultDTO {
    long count;
    List<ProductDocument> results;
}

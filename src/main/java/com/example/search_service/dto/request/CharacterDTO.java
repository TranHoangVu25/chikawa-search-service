package com.example.search_service.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharacterDTO {
    private String name;
    private String slug;
    @JsonCreator
    public CharacterDTO(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }
}

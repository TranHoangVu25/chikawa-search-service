package com.example.search_service.configuration;

import co.elastic.clients.json.JsonpMapper;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {
    @Bean
    public JsonpMapper jsonpMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // Đăng ký module JavaTimeModule
        objectMapper.registerModule(new JavaTimeModule());

        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return new JacksonJsonpMapper(objectMapper);
    }
}

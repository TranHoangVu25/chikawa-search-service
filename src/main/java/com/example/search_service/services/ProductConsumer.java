package com.example.search_service.services;

import com.example.search_service.models.ProductDocument;
import com.example.search_service.models.ProductSearchEvent;
import com.example.search_service.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductConsumer {

    private final ProductRepository repository;

    @RabbitListener(queues = "search_service_queue")
    public void consume(ProductSearchEvent event) {
        System.out.println("📥 Received: " + event.getName());

        ProductDocument doc = ProductDocument.builder()
                .id(event.getId())
                .name(event.getName())
                .description(event.getDescription())
                .price(event.getPrice())
                .categories(event.getCategories())
                .characters(event.getCharacters())
                .build();

        repository.save(doc);
        System.out.println("✅ Indexed product in Elasticsearch: " + event.getId());
    }
}

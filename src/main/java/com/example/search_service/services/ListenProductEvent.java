package com.example.search_service.services;

import com.example.search_service.Enums.Action;
import com.example.search_service.dto.request.CategoryDTO;
import com.example.search_service.dto.request.CharacterDTO;
import com.example.search_service.exception.ErrorCode;
import com.example.search_service.models.ProductDocument;
import com.example.search_service.models.ProductReceiveEvent;
import com.example.search_service.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ListenProductEvent {
    private final ProductRepository repository;

    //receive data from create product
    public void listenCreateProduct(ProductReceiveEvent event) {
        try {
            if (Objects.equals(event.getAction(), Action.CREATE)) {
                System.out.println("Received: " + event.getName());
                System.out.println("in create product");
                System.out.println("event:"+event);
                ProductDocument doc = ProductDocument.builder()
                .id(event.getId())
                .name(event.getName())
                .price(event.getPrice())
                        .status(event.getStatus())
                .categories(
                        event.getCategories() != null
                                ? event.getCategories().stream()
                                .map(CategoryDTO::getName) // hoặc getSlug()
                                .toList()
                                : List.of()
                )
                .characters(
                        event.getCharacters() != null
                                ? event.getCharacters().stream()
                                .map(CharacterDTO::getName)
                                .toList()
                                : List.of()
                )
                        .images(
                                event.getImages() != null && !event.getImages().isEmpty()
                                        ? List.of(event.getImages().get(0))
                                        : List.of()
                        )
                        .build();

        repository.save(doc);
        System.out.println("Indexed product in Elasticsearch: " + event.getId());
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public void listenUpdateProduct(ProductReceiveEvent event) {
        try {
            if (Objects.equals(event.getAction(), Action.UPDATE)) {

                System.out.println("Received: " + event.getName());

                ProductDocument d = repository.findById(event.getId())
                        .orElseThrow(() -> new RuntimeException("Product not found"));

                d.setName(event.getName());
                d.setPrice(event.getPrice());
                d.setStatus(event.getStatus());
                d.setCategories(event.getCategories() != null
                        ? event.getCategories()
                        .stream()
                        .map(CategoryDTO::getName)
                        .toList()
                        : List.of()
                );
                d.setCharacters(event.getCharacters() != null
                        ? event.getCharacters()
                        .stream()
                        .map(CharacterDTO::getName)
                        .toList()
                        : List.of()
                );
                d.setImages(
                        event.getImages() != null && !event.getImages().isEmpty()
                                ? List.of(event.getImages().get(0))
                                : List.of()
                );
                repository.save(d);

                System.out.println("Update. Indexed product in Elasticsearch: " + event.getId());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void listenDeleteProduct(ProductReceiveEvent e) {
        try {
            if (e.getAction().equals(Action.DELETE)) {
            System.out.println("Received: " + e.getId());
            if(!repository.existsById(e.getId())){
                throw new RuntimeException(ErrorCode.PRODUCT_NOT_EXISTED.getMessage());
            }
            repository.deleteById(e.getId());

            System.out.println("Delete product in Elasticsearch has id: " + e.getId());
            }
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new RuntimeException(exception);
        }
    }

    @RabbitListener(queues = "product_search_queue")
    public void controlEvent(ProductReceiveEvent event) {
        switch (event.getAction()) {
            case CREATE -> listenCreateProduct(event);
            case UPDATE -> listenUpdateProduct(event);
            case DELETE -> listenDeleteProduct(event);
            default -> log.error("Error in sending message");
        }
    }
}

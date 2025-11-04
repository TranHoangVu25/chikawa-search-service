package com.example.search_service.services;

import com.example.search_service.dto.request.CategoryDTO;
import com.example.search_service.dto.request.CharacterDTO;
import com.example.search_service.exception.ErrorCode;
import com.example.search_service.models.ProductDocument;
import com.example.search_service.models.ProductSearchEvent;
import com.example.search_service.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ListenProductEvent {
    private final ProductRepository repository;

    //receive data from create product
    @RabbitListener(queues = "search_service_queue")
    public void listenCreateProduct(ProductSearchEvent event) {
        System.out.println("Received: " + event.getName());

        ProductDocument doc = ProductDocument.builder()
                .id(event.getId())
                .name(event.getName())
//                .description(event.getDescription())
                .price(event.getPrice())
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
                .build();

        repository.save(doc);
        System.out.println("Indexed product in Elasticsearch: " + event.getId());
    }


//    @RabbitListener(queues = "search_service_queue")
//    public void listenUpdateProduct(ProductSearchEvent event) {
//        try {
//        System.out.println("Received: " + event.getName());
//
//        ProductDocument d = repository.findById(event.getId())
//                        .orElseThrow(()-> new RuntimeException("Product not found"));
//
//        d.setName(event.getName());
//        d.setDescription(event.getDescription());
//        d.setPrice(event.getPrice());
//        d.setCategories(event.getCategories());
//        d.setCharacters(event.getCharacters());
//
//        repository.save(d);
//
//        System.out.println("Indexed product in Elasticsearch: " + event.getId());
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            throw new RuntimeException(e);
//        }
//        }
    @RabbitListener(queues = "search_service_queue")
    public void listenDeleteProduct(ProductSearchEvent e) {
        try {
            System.out.println("Received: " + e.getId());
            if(!repository.existsById(e.getId())){
                throw new RuntimeException(ErrorCode.PRODUCT_NOT_EXISTED.getMessage());
            }
            repository.deleteById(e.getId());

            System.out.println("Delete product in Elasticsearch has id: " + e.getId());
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new RuntimeException(exception);
        }
    }
}

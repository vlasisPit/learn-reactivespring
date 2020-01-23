package com.learnreactivespring.repository;

import com.learnreactivespring.document.Item;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

/**
 * Execute all necessary mongoDB actions
 * ReactiveMongoRepository<Item, String>
 *     Item => something to store in Mongo
 *     String => type of the key
 */
public interface ItemReactiveRepository extends ReactiveMongoRepository<Item, String> {

     /**
      * Custom read method
      * Custom reactive operation to read from Mongo. You need to do this when the operation on ReactiveMongoRepository
      * does not exist
      * The correlation is done by the name of the method eg findByDescription
      * If the method name is findByDescrion, this will not work
      */
      Mono<Item> findByDescription(String description);
}
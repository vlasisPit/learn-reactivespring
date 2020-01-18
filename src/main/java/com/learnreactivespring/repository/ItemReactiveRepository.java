package com.learnreactivespring.repository;

import com.learnreactivespring.document.Item;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * Execute all necessary mongoDB actions
 * ReactiveMongoRepository<Item, String>
 *     Item => something to store in Mongo
 *     String => type of the key
 */
public interface ItemReactiveRepository extends ReactiveMongoRepository<Item, String> {

 }

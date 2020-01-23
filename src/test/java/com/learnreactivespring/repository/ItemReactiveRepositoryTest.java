package com.learnreactivespring.repository;

import com.learnreactivespring.document.Item;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest      //It will load only the necessary classes to test the mongoDB Application
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ItemReactiveRepositoryTest {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    //Id as null and the id will be created from the databse once inserted
    List<Item> itemList = Arrays.asList(
            new Item(null, "Samsung TV", 400.0),
            new Item(null, "LG TV", 420.0)
            ,new Item(null, "Apple Watch", 299.99),
            new Item(null, "Beats Headphones", 149.99),
            new Item("ABC", "Bose Headphones", 149.99)
    );

    @BeforeAll
    public void setUp() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(itemList))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> System.out.println("Inserted item is : " + item))
                .blockLast();   //Wait until all the operations above are completed.
    }

    /**
     * This test does not use an embedded mongo but the containerized Mongo from application properties.
     */
    @Test
    public void getAllItems() {
        StepVerifier.create(itemReactiveRepository.findAll())
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    public void getItemById() {
        StepVerifier.create(itemReactiveRepository.findById("ABC"))
                .expectSubscription()
                .expectNextMatches(item -> "Bose Headphones".equals(item.getDescription()))
                .verifyComplete();
    }

    @Test
    public void findItemByDescription() {
        StepVerifier.create(itemReactiveRepository.findByDescription("Bose Headphones").log("findItemByDescription: "))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

    /**
     * Save in a non-blocking way
     */
    @Test
    public void saveItem() {
        Item item = new Item(null, "Google Home Mini", 30.00);
        Mono<Item> savedItem = itemReactiveRepository.save(item).log("savedItem : ");
        StepVerifier.create(savedItem)
                .expectSubscription()
                .expectNextMatches(item1 -> item1.getId() != null && "Google Home Mini".equals(item1.getDescription()))
                .verifyComplete();
    }

    @Test
    public void updateItem() {
        double newPrice = 520.00;
        Mono<Item> lg_tv = itemReactiveRepository.findByDescription("LG TV")
                .map(item -> {
                    item.setPrice(newPrice);    //setting the new price
                    return item;
                })
                .flatMap(item -> itemReactiveRepository.save(item));    //saving the item with the new price

        StepVerifier.create(lg_tv)
                .expectSubscription()
                .expectNextMatches(item -> item.getPrice() == 520.00)
                .verifyComplete();
    }

    @Test
    public void deleteItemById() {
        Mono<Object> deletedItem = itemReactiveRepository.findById("ABC")      //gives back the Mono<Item>
                .map(Item::getId)
                .flatMap(id -> itemReactiveRepository.deleteById(id));

        StepVerifier.create(deletedItem.log())
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll().log("The new Item List : "))
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    public void deleteItem() {
        Mono<Object> deletedItem = itemReactiveRepository.findByDescription("LG TV")      //gives back the Mono<Item>
                .flatMap(item -> itemReactiveRepository.delete(item));

        StepVerifier.create(deletedItem.log())
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll().log("The new Item List : "))
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

}

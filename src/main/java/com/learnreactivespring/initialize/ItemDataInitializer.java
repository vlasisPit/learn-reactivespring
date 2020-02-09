package com.learnreactivespring.initialize;

import com.learnreactivespring.document.Item;
import com.learnreactivespring.document.ItemCapped;
import com.learnreactivespring.repository.ItemReactiveCappedRepository;
import com.learnreactivespring.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

/**
 * Set this class to Component in order to be scanned during the application start up
 */
@Component
@Slf4j
@Profile("!test")
public class ItemDataInitializer implements CommandLineRunner {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @Autowired
    ItemReactiveCappedRepository itemReactiveCappedRepository;

    @Autowired
    MongoOperations mongoOperations;

    @Override
    public void run(String... args) throws Exception {
        initialDataSetUp();
        createCappedCollection();
        dataSetupForCappedCollection();
    }

    /**
     * Drop capped collection fast in every application start up.
     */
    private void createCappedCollection() {
        mongoOperations.dropCollection(ItemCapped.class);
        mongoOperations.createCollection(
                ItemCapped.class,       //Document type
                CollectionOptions.empty()
                        .maxDocuments(20)   //max documents to store at a given point
                        .size(5000)         //whole capped collection size
                        .capped()
        );
    }

    private void initialDataSetUp() {
        itemReactiveRepository.deleteAll()      //delete all the data from MongoDB
                .thenMany(Flux.fromIterable(data()))
                .flatMap(itemReactiveRepository::save)
                .thenMany(itemReactiveRepository.findAll())
                .subscribe(item -> System.out.println("Item inserted from CommandLineRunner : " + item));       //NEVER block here, only subscribe
    }

    private List<Item> data() {
        return Arrays.asList(
                new Item(null, "Samsung TV", 399.99),
                new Item(null, "LG TV", 329.99),
                new Item(null, "Apple Watch", 349.99),
                new Item("ABC", "Beats HeadPhones", 19.99)
        );
    }

    public void dataSetupForCappedCollection() {
        Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofSeconds(1))    //emit 1,2,3,4... every second
                .map(i -> new ItemCapped(null, "Random item " + i, (100.00 + i)));

        //subscribe to flux and save each ItemCapped
        itemReactiveCappedRepository.insert(itemCappedFlux)
                .subscribe(itemCapped -> log.info("Inserted item is " + itemCapped)); //print each saved itemCapped
    }
}

package com.learnreactivespring.controller.v1;

import com.learnreactivespring.document.Item;
import com.learnreactivespring.repository.ItemReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static com.learnreactivespring.constants.ItemConstants.ITEM_END_POINT_V1;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ItemControllerTest {

    /**
     * Inject a non-blocking client
     */
    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @BeforeEach
    public void setUp() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(data()))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> System.out.println("Inserted item is : " + item))
                .blockLast();       //block because we need all the data to be inserted in Mongo before continue with testing
    }

    private List<Item> data() {
        return Arrays.asList(
                new Item(null, "Samsung TV", 399.99),
                new Item(null, "LG TV", 329.99),
                new Item(null, "Apple Watch", 349.99),
                new Item("ABC", "Beats HeadPhones", 19.99)
        );
    }

    @Test
    public void getAllItems() {
        webTestClient.get()
                .uri(ITEM_END_POINT_V1)
                .exchange()     //connect to the endpoint
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(4);
    }

    @Test
    public void getAllItems_approach2() {
        webTestClient.get()
                .uri(ITEM_END_POINT_V1)
                .exchange()     //connect to the endpoint
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(4)
                .consumeWith(response -> {
                    List<Item> items = response.getResponseBody();
                    items.forEach(
                            item -> assertTrue(item.getId() != null)
                    );
                });
    }

    @Test
    public void getAllItems_approach3() {
        Flux<Item> itemsFlux = webTestClient.get()
                .uri(ITEM_END_POINT_V1)
                .exchange()     //connect to the endpoint
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .returnResult(Item.class)
                .getResponseBody();

        StepVerifier.create(itemsFlux.log("Value from network : "))
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    public void getOneItem() {
        webTestClient.get()
                .uri(ITEM_END_POINT_V1.concat("/{id}"), "ABC")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price", 149.99);
    }

    @Test
    public void getOneItem_notFound() {
        webTestClient.get()
                .uri(ITEM_END_POINT_V1.concat("/{id}"), "DEF")
                .exchange()
                .expectStatus().isNotFound();
    }
}

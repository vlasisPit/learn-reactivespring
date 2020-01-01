package com.learnreactivespring.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This test is for the returnFlux method, which does not return a stream of events,
 * but all the events together when the flux is completed.
 */
//In JUnit 5, the @RunWith annotation has been replaced by the more powerful @ExtendWith annotation.
//@RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class)
@WebFluxTest    //scan for all the classes that are annotated with @RestController, @Controller and more
                //This will not scan @Component, @Service, @Repository
public class FluxAndMonoControllerTest {

    /**
     * We need a non-blocking client
     * Equivalent to RestTemplate from Spring MVC
     * The @WebFluxTest annotation is responsible for creating the WebTestClient instance.
     */
    @Autowired
    WebTestClient webTestClient;

    @Test
    public void flux_approach1() {
        Flux<Integer> integerFlux = webTestClient.get()     //GET call to the endpoint
                .uri("/flux")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()     //actual call to the endpoint
                .returnResult(Integer.class)
                .getResponseBody();

        StepVerifier.create(integerFlux)
                .expectSubscription()
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .expectNext(4)
                .verifyComplete();
    }

    @Test
    public void flux_approach2() {
        webTestClient.get()
                .uri("/flux")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBodyList(Integer.class)
                .hasSize(4);
    }

    @Test
    public void flux_approach3() {
        List<Integer> expected = Arrays.asList(1,2,3,4);

        EntityExchangeResult<List<Integer>> entityExchangeResult =
                webTestClient.get()
                .uri("/flux")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)      //convert flux to a list
                .returnResult();

        assertEquals(expected, entityExchangeResult.getResponseBody());
    }

    @Test
    public void flux_approach4() {
        List<Integer> expected = Arrays.asList(1,2,3,4);

        webTestClient.get()
                .uri("/flux")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)      //convert flux to a list
                .consumeWith(response -> {
                    assertEquals(expected, response.getResponseBody());
                });
    }

}

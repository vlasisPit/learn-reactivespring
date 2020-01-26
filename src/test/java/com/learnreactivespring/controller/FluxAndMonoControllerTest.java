package com.learnreactivespring.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
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
@SpringBootTest
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
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

    /**
     * It is sufficient to check the first 3 elements, that's why we cancel the
     * subscription (because the flux is infinite)
     */
    @Test
    public void fluxStream() {
        Flux<Long> longStreamFlux = webTestClient.get()     //GET call to the endpoint
                .uri("/fluxstream-infinite")
                .accept(MediaType.APPLICATION_STREAM_JSON)      //!!!!!!!!!!!!!!! SOS
                .exchange()     //actual call to the endpoint
                .expectStatus().isOk()
                .returnResult(Long.class)
                .getResponseBody();

        StepVerifier.create(longStreamFlux)
                .expectNext(0L)
                .expectNext(1L)
                .expectNext(2L)
                .thenCancel()
                .verify();
    }

    /**
     * We can use Mono when you are requesting for one single resource and you want the resource in a
     * non-blocking way.
     */
    @Test
    public void mono() {
        Integer expected = 1;

        webTestClient.get()
                .uri("/mono")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .consumeWith(response -> {
                    assertEquals(expected, response.getResponseBody());
                });
    }


}

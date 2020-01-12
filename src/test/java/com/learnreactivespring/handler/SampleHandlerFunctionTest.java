package com.learnreactivespring.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

//In JUnit 5, the @RunWith annotation has been replaced by the more powerful @ExtendWith annotation.
//@RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest //because we need to scan @Component
@AutoConfigureWebTestClient     //it is necessary in order to Autowire the WebTestClient
public class SampleHandlerFunctionTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    public void flux_approach1() {
        Flux<Integer> integerFlux = webTestClient.get()     //GET call to the endpoint
                .uri("/functional/flux")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()     //actual call to the endpoint
                .expectStatus().isOk()
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
    public void mono() {
        Integer expected = 1;

        webTestClient.get()
                .uri("/functional/mono")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .consumeWith(response -> {
                    assertEquals(expected, response.getResponseBody());
                });
    }
}

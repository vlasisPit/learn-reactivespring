package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoErrorTest {

    /**
     * Resume with onComplete but do not continue to the next element of the flux.
     */
    @Test
    public void fluxErrorHandling_onErrorResume() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .concatWith(Flux.just("D"))
                .onErrorResume((e) -> {     //this block will executed whenever there is an error
                    System.out.println("Exception is : " + e);
                    return Flux.just("default", "default1");
                });

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("default", "default1")
                .verifyComplete();
        //.expectError(RuntimeException.class)
        //.verify();
    }

    /**
     * With onErrorReturn, we return a fallback value and not a Flux.
     */
    @Test
    public void fluxErrorHandling_onErrorReturn() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .concatWith(Flux.just("D"))
                .onErrorReturn("default");

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("default")
                .verifyComplete();
    }

    /**
     * With , assign an exception from one type to another type eg a created custom exception.
     */
    @Test
    public void fluxErrorHandling_onErrorMap() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .concatWith(Flux.just("D"))
                .onErrorMap(e -> new CustomException(e));

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectError(CustomException.class)
                .verify();
    }

    /**
     * Handle exceptions with retry. You can retry as many times as you want but you will repeat all the sequence.
     * Runs on the same thread.
     */
    @Test
    public void fluxErrorHandling_onErrorMap_withRetry() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .concatWith(Flux.just("D"))
                .onErrorMap(e -> new CustomException(e))
                .retry(2);

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectError(CustomException.class)
                .verify();
    }

    /**
     * 5 sec backoff before execute a retry. Wait for 5 sec before retrying.
     * Run on different thread.
     */
    @Test
    public void fluxErrorHandling_onErrorMap_withRetryBackoff() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .concatWith(Flux.just("D"))
                .onErrorMap(e -> new CustomException(e))
                .retryBackoff(2, Duration.ofSeconds(5));

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectError(IllegalStateException.class)   //Because of backoff, the exception type is IllegalStateException
                .verify();
    }
}

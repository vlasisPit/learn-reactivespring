package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

import java.time.Duration;

public class FluxAndMonoCombineTest {

    @Test
    public void combineUsingMerge() {
        //Maybe you want to do 2 different external calls to DBs or external services and then combine the results.
        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F");

        Flux<String> mergedFlux = Flux.merge(flux1, flux2);

        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    public void combineUsingMerge_withDelay() {
        //Maybe you want to do 2 different external calls to DBs or external services and then combine the results.
        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        Flux<String> mergedFlux = Flux.merge(flux1, flux2);

        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                //.expectNext("A", "B", "C", "D", "E", "F")
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    public void combineUsingConcat() {
        //Maybe you want to do 2 different external calls to DBs or external services and then combine the results.
        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F");

        Flux<String> mergedFlux = Flux.concat(flux1, flux2);

        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    /**
     * Check VirtualTimeTest.java
     * This test is optimized in order not to take 6 sec to complete
     */
    @Test
    public void combineUsingConcat_withDelay() {
        //Maybe you want to do 2 different external calls to DBs or external services and then combine the results.
        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        Flux<String> mergedFlux = Flux.concat(flux1, flux2);

        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    public void combineUsingZip() {
        //Maybe you want to do 2 different external calls to DBs or external services and then combine the results.
        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F");

        //It returns [A,D] [B,E] [C,F]
        Flux<Tuple2<String, String>> mergedFlux = Flux.zip(flux1, flux2);

        //It returns AD BE CF
        Flux<String> mergedFlux1 = Flux.zip(flux1, flux2, (t1, t2) -> t1.concat(t2));

        StepVerifier.create(mergedFlux1.log())
                .expectSubscription()
                .expectNext("AD", "BE", "CF")
                .verifyComplete();
    }

    @Test
    public void combineUsingZip_differentSizes() {
        //Maybe you want to do 2 different external calls to DBs or external services and then combine the results.
        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F", "G", "I");

        //It returns [A,D] [B,E] [C,F]
        Flux<Tuple2<String, String>> mergedFlux = Flux.zip(flux1, flux2);

        //It returns AD BE CF
        Flux<String> mergedFlux1 = Flux.zip(flux1, flux2, (t1, t2) -> t1.concat(t2));

        //You cannot have both expectNext and expectNextCount on the same subscribe.
        StepVerifier.create(mergedFlux1.log())
                .expectSubscription()
                .expectNext("AD", "BE", "CF")
                .verifyComplete();

        StepVerifier.create(mergedFlux1.log())
                .expectSubscription()
                .expectNextCount(3)
                .verifyComplete();
    }
}

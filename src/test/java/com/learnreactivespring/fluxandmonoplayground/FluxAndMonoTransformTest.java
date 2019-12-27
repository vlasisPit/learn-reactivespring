package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoTransformTest {

    List<String> names = Arrays.asList("adam", "anna", "jack", "jenny");

    @Test
    public void transformUsingMap() {
        Flux<String> namesFlux = Flux.fromIterable(names)
                .map(s -> s.toUpperCase())
                .log();

        StepVerifier.create(namesFlux)
                .expectNext("ADAM", "ANNA", "JACK", "JENNY")
                .verifyComplete();
    }

    @Test
    public void transformUsingMap_Length() {
        Flux<Integer> namesFlux = Flux.fromIterable(names)
                .map(s -> s.length())
                .log();

        StepVerifier.create(namesFlux)
                .expectNext(4, 4, 4, 5)
                .verifyComplete();
    }

    @Test
    public void transformUsingMap_Length_repeat() {
        Flux<Integer> namesFlux = Flux.fromIterable(names)
                .map(s -> s.length())
                .repeat(1)
                .log();

        StepVerifier.create(namesFlux)
                .expectNext(4, 4, 4, 5, 4, 4, 4, 5)
                .verifyComplete();
    }

    @Test
    public void transformUsingMap_Filter() {
        Flux<String> namesFlux = Flux.fromIterable(names)
                .filter(s -> s.length() > 4)
                .map(s -> s.toUpperCase())
                .log();

        StepVerifier.create(namesFlux)
                .expectNext("JENNY")
                .verifyComplete();
    }

    /**
     * Flatmap is necessary if you want to call a DB or external service for each and every element
     * and the response will be a flux (a publisher) also s -> Flux<String>
     */
    @Test
    public void transformUsingFlatMap() {
        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .flatMap(s -> {
                    return Flux.fromIterable(convertToList(s));     //A -> List[A, newValue], B -> List[B, newValue]
                })      //simulate a call to a service or DB
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    private List<String> convertToList(String s) {
        try {
            Thread.sleep(1000);         //this is an external call. Wait for 1 second
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(s, "newValue");
    }

    /**
     * This parallel execution does not maintain order
     */
    @Test
    public void transformUsingFlatMap_usingParallel() {
        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .window(2)  //pass 2 elements instead one by one and returns Flux<Flux<String>>
                .flatMap(s -> {
                   return s.map(this::convertToList)
                           .subscribeOn(Schedulers.parallel())  //change a thread to another thread. All execution happens in parallel. returns a Flux<List<String>>
                           .flatMap(Flux::fromIterable);        //Flux<String>
                })
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    /**
     * concatMap maintains order but it needs 6 sec as the serial execution.
     */
    @Test
    public void transformUsingFlatMap_usingParallel_maintain_order_butSlow() {
        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .window(2)  //pass 2 elements instead one by one and returns Flux<Flux<String>>
                .concatMap(s -> {       //same operation as flatMap but it maintains order
                    return s.map(this::convertToList)
                            .subscribeOn(Schedulers.parallel())  //change a thread to another thread. All execution happens in parallel. returns a Flux<List<String>>
                            .flatMap(Flux::fromIterable);        //Flux<String>
                })
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    /**
     * flatMapSequential maintains order but it needs 6 sec as the serial execution.
     */
    @Test
    public void transformUsingFlatMap_usingParallel_maintain_order_butFast() {
        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .window(2)  //pass 2 elements instead one by one and returns Flux<Flux<String>>
                .flatMapSequential(s -> {       //same operation as flatMap but it maintains order
                    return s.map(this::convertToList)
                            .subscribeOn(Schedulers.parallel())  //change a thread to another thread. All execution happens in parallel. returns a Flux<List<String>>
                            .flatMap(Flux::fromIterable);        //Flux<String>
                })
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }
}

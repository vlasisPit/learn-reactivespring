package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class ColdAndHotPublisherTest {

    /**
     * Cold publisher
     * Everytime a subscriber subscribes to the publisher, it emits the events from the beginning
     * @throws InterruptedException
     */
    @Test
    public void coldPublisherTest() throws InterruptedException {
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F")
                .delayElements(Duration.ofSeconds(1));

        stringFlux.subscribe(s -> System.out.println("Subscriber 1 : " + s));   //Cold publisher, because it emits the events from the beginning
        Thread.sleep(2000);

        stringFlux.subscribe(s -> System.out.println("Subscriber 2 : " + s));   //Cold publisher, because it emits the events from the beginning
        Thread.sleep(4000);
    }

    /**
     * Hot publisher is the exact opposite of the cold publisher. It is not going to emit events from the
     * beginning for any new subscriber that gets subscribed to the flux.
     */
    @Test
    public void hotPublisherTest() throws InterruptedException {
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F")
                .delayElements(Duration.ofSeconds(1));

        ConnectableFlux<String> connectableFlux = stringFlux.publish();
        connectableFlux.connect();

        connectableFlux.subscribe(s -> System.out.println("Subscriber 1 : " + s));
        Thread.sleep(3000);

        connectableFlux.subscribe(s -> System.out.println("Subscriber 2 : " + s));  //does not emit the values from beginning
        Thread.sleep(4000);
    }
}

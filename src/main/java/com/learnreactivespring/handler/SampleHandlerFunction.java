package com.learnreactivespring.handler;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * THIS IS THE HANDLER FUNCTION !!!!!!!!!!!!!!
 * Component because we want this class to be scanned as a bean
 */
@Component
public class SampleHandlerFunction {

    public Mono<ServerResponse> flux(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        Flux.just(1,2,3,4)
                        .log(), Integer.class
                );
    }

    public Mono<ServerResponse> mono(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        Mono.just(1)
                                .log(), Integer.class
                );
    }
}

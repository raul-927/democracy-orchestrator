package com.democracy.democracy_orchestrator.infrastructure.publisher;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;

@Component
public class EventPublisher {

    private final Sinks.Many<String> eventSink;

    private Disposable intervalSubscription;

    public EventPublisher() {
        this.eventSink = Sinks.many().multicast().onBackpressureBuffer();
    }

    @PostConstruct
    public void init() {
        intervalSubscription = Flux.interval(Duration.ofSeconds(1))
                .map(sequence -> "Event " + sequence)
                .doOnNext(event -> {
                    Sinks.EmitResult result = eventSink.tryEmitNext(event);
                    if (result.isFailure()) {
                        // Log the failure or take appropriate action
                        System.err.println("Emission failed: " + result);
                    }
                })
                .subscribe();
    }

    public Flux<String> getEventStream() {
        return eventSink.asFlux();
    }

    @PreDestroy
    public void cleanup() {
        if (intervalSubscription != null && !intervalSubscription.isDisposed()) {
            intervalSubscription.dispose();
        }
    }
}
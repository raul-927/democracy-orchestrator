package com.democracy.democracy_orchestrator.infrastructure.publisher;

import com.democracy.democracy_orchestrator.domain.models.InvestigationResult;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;

@Component
public class EventPublisher {

    private final Sinks.Many<String> eventSink;
    private Disposable intervalSubscription;

    public EventPublisher() {
        this.eventSink = Sinks.many().multicast().onBackpressureBuffer();
    }

    //@PostConstruct
    public void init(InvestigationResult investigationResult) {
        intervalSubscription = Flux.just(investigationResult)
               // .map(sequence -> "Evento " + sequence)
                .doOnNext(event -> {
                    System.out.println("Next Event: "+event);
                    Sinks.EmitResult result = eventSink.tryEmitNext(event.toString());
                    System.out.println("Result: "+result);
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
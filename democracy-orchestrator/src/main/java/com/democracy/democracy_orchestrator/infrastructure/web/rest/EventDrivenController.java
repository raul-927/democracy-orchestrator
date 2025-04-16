package com.democracy.democracy_orchestrator.infrastructure.web.rest;


import com.democracy.democracy_orchestrator.infrastructure.publisher.EventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class EventDrivenController {
    @Autowired
    private EventPublisher eventPublisher;

    @GetMapping("/events")
    public Flux<String> getEventStream() {
        return eventPublisher.getEventStream();
    }
}

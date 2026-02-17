package com.democracy.democracy_orchestrator.infrastructure.web.rest;


import com.democracy.democracy_orchestrator.infrastructure.publisher.EventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/democracyorchestrator")
public class EventDrivenController {
    @Autowired
    private EventPublisher eventPublisher;

    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getEventStream() {
        return eventPublisher.getEventStream();
    }
}

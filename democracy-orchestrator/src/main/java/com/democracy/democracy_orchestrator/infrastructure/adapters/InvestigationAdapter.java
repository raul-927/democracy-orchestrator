package com.democracy.democracy_orchestrator.infrastructure.adapters;

import com.democracy.democracy_orchestrator.application.services.TokenService;
import com.democracy.democracy_orchestrator.domain.models.Investigation;
import com.democracy.democracy_orchestrator.domain.models.Person;
import com.democracy.democracy_orchestrator.domain.ports.out.InvestigationOut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
public class InvestigationAdapter implements InvestigationOut {

    @Autowired
    private WebClient webClient;

    @Autowired
    private TokenService tokenService;

    @Override
    public Flux<Investigation> selectInvestigation(Investigation investigation) {
        BodyInserter<Investigation, ReactiveHttpOutputMessage> selectInvestigation = BodyInserters.fromValue(investigation);
        return webClient.post()
                .uri("http://localhost:8082/electoralcourt/investigation/select")
                .headers((headers) -> headers.add("authorization", tokenService.obtainToken()))
                .body(selectInvestigation)
                .retrieve()
                .bodyToFlux(Investigation.class);
    }

    @Override
    public Flux<Investigation> sendInvestigation(Investigation investigation) {
        BodyInserter<Investigation, ReactiveHttpOutputMessage> selectInvestigation = BodyInserters.fromValue(investigation);
        return webClient.post()
                .uri("http://localhost:8082/electoralcourt/investigation/insert")
                .headers((headers) -> headers.add("authorization", tokenService.obtainToken()))
                .body(selectInvestigation)
                .retrieve()
                .bodyToFlux(Investigation.class);
    }
}

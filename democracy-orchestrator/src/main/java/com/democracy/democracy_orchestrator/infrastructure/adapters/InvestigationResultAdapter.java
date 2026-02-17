package com.democracy.democracy_orchestrator.infrastructure.adapters;

import com.democracy.democracy_orchestrator.application.services.TokenService;
import com.democracy.democracy_orchestrator.domain.models.InvestigationResult;
import com.democracy.democracy_orchestrator.domain.ports.out.InvestigationResultOut;
import com.democracy.democracy_orchestrator.infrastructure.publisher.EventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static com.democracy.democracy_orchestrator.infrastructure.config.UrlConstant.*;

@Component
public class InvestigationResultAdapter implements InvestigationResultOut {

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private WebClient webClient;

    @Autowired
    private TokenService tokenService;

    @Override
    public Mono<Integer> calculateScore(InvestigationResult investigationResult) {
        eventPublisher.init(investigationResult);
        BodyInserter<InvestigationResult, ReactiveHttpOutputMessage> sendInvestigationResult = BodyInserters.fromValue(investigationResult);
        return webClient.post()
                .uri(LOCAL_HOST_8082 + ELECTORAL_COURT + INVESTIGATION_RESULT + INSERT)
                .headers((headers) -> headers.add("authorization", tokenService.obtainToken()))
                .body(sendInvestigationResult)
                .retrieve()
                .bodyToMono(Integer.class);
    }

    @Override
    public Mono<Integer> sendInvestigationResult(InvestigationResult investigationResult) {
        BodyInserter<InvestigationResult, ReactiveHttpOutputMessage> sendInvestigationResult = BodyInserters.fromValue(investigationResult);
        return webClient.post()
                .uri(LOCAL_HOST_8082 + ELECTORAL_COURT + INVESTIGATION_RESULT + INSERT)
                .headers((headers) -> headers.add("authorization", tokenService.obtainToken()))
                .body(sendInvestigationResult)
                .retrieve()
                .bodyToMono(Integer.class);
    }
}
package com.democracy.democracy_orchestrator.infrastructure.adapters;

import com.democracy.democracy_orchestrator.application.services.TokenService;
import com.democracy.democracy_orchestrator.domain.models.Investigation;
import com.democracy.democracy_orchestrator.domain.ports.out.InvestigationOut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;

import static com.democracy.democracy_orchestrator.infrastructure.config.UrlConstant.*;

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
                .uri(LOCAL_HOST_8082 + ELECTORAL_COURT + INVESTIGATION + SELECT)
                .headers((headers) -> headers.add("authorization", tokenService.obtainToken()))
                .body(selectInvestigation)
                .retrieve()
                .bodyToFlux(Investigation.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                        .filter(throwable -> throwable instanceof WebClientResponseException)
                        .onRetryExhaustedThrow((spec, signal) -> signal.failure()));
    }

    @Override
    public Flux<Investigation> sendInvestigation(Investigation investigation) {
        BodyInserter<Investigation, ReactiveHttpOutputMessage> selectInvestigation = BodyInserters.fromValue(investigation);
        return webClient.post()
                .uri(LOCAL_HOST_8082 + ELECTORAL_COURT + INVESTIGATION + INSERT)
                .headers((headers) -> headers.add("authorization", tokenService.obtainToken()))
                .body(selectInvestigation)
                .retrieve()
                .bodyToFlux(Investigation.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                        .filter(throwable -> throwable instanceof WebClientResponseException)
                        .onRetryExhaustedThrow((spec, signal) -> signal.failure()));
    }
}

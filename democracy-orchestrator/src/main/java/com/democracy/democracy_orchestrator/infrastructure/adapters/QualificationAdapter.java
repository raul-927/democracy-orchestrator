package com.democracy.democracy_orchestrator.infrastructure.adapters;

import com.democracy.democracy_orchestrator.application.services.TokenService;
import com.democracy.democracy_orchestrator.domain.models.Qualification;
import com.democracy.democracy_orchestrator.domain.ports.out.QualificationOut;
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

import static com.democracy.democracy_orchestrator.infrastructure.config.UrlConstant.LOCAL_HOST_8082;
import static com.democracy.democracy_orchestrator.infrastructure.config.UrlConstant.HUMAN_RESOURCES;
import static com.democracy.democracy_orchestrator.infrastructure.config.UrlConstant.QUALIFICATION;
import static com.democracy.democracy_orchestrator.infrastructure.config.UrlConstant.SELECT;
@Component
public class QualificationAdapter implements QualificationOut {


    @Autowired
    private WebClient webClient;

    @Autowired
    private TokenService tokenService;

    @Override
    public Flux<Qualification> selectQualification(Qualification qualification) {
        BodyInserter<Qualification, ReactiveHttpOutputMessage> insertQualification = BodyInserters.fromValue(qualification);
        return webClient.post()
                .uri(LOCAL_HOST_8082 + HUMAN_RESOURCES + QUALIFICATION + SELECT)
                .headers((headers) -> headers.add("authorization", tokenService.obtainToken()))
                .body(insertQualification)
                .retrieve()
                .bodyToFlux(Qualification.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                        .filter(throwable -> throwable instanceof WebClientResponseException)
                        .onRetryExhaustedThrow((spec, signal) -> signal.failure()));
    }
}

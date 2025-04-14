package com.democracy.democracy_orchestrator.infrastructure.adapters;

import com.democracy.democracy_orchestrator.application.services.TokenService;
import com.democracy.democracy_orchestrator.domain.models.Investigation;
import com.democracy.democracy_orchestrator.domain.models.Profession;
import com.democracy.democracy_orchestrator.domain.ports.out.ProfessionOut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
@Component
public class ProfessionAdapter implements ProfessionOut {

    @Autowired
    private WebClient webClient;

    @Autowired
    private TokenService tokenService;

    @Override
    public Flux<Profession> selectProfession(Profession profession) {
        BodyInserter<Profession, ReactiveHttpOutputMessage> inserterProfession = BodyInserters.fromValue(profession);
        return webClient.post()
                .uri("http://localhost:8082/humanresources/profession/select")
                .headers((headers) -> headers.add("authorization", tokenService.obtainToken()))
                .body(inserterProfession)
                .retrieve()
                .bodyToFlux(Profession.class);
    }
}

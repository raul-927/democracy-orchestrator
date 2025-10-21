package com.democracy.democracy_orchestrator.infrastructure.adapters;

import com.democracy.democracy_orchestrator.application.services.TokenService;
import com.democracy.democracy_orchestrator.domain.models.Document;
import com.democracy.democracy_orchestrator.domain.ports.out.DocumentOut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import static com.democracy.democracy_orchestrator.infrastructure.config.UrlConstant.LOCAL_HOST_8082;
import static com.democracy.democracy_orchestrator.infrastructure.config.UrlConstant.HUMAN_RESOURCES;
import static com.democracy.democracy_orchestrator.infrastructure.config.UrlConstant.DOCUMENT;
import static com.democracy.democracy_orchestrator.infrastructure.config.UrlConstant.SELECT;

@Component
public class DocumentAdapter implements DocumentOut {

    @Autowired
    private WebClient webClient;

    @Autowired
    private TokenService tokenService;

    @Override
    public Flux<Document> selectDocument(Document document) {
        BodyInserter<Document, ReactiveHttpOutputMessage> insertDocument = BodyInserters.fromValue(document);
        return webClient.post()
                .uri(LOCAL_HOST_8082 + HUMAN_RESOURCES + DOCUMENT + SELECT)
                .headers((headers) -> headers.add("authorization", tokenService.obtainToken()))
                .body(insertDocument)
                .retrieve()
                .bodyToFlux(Document.class);
    }
}

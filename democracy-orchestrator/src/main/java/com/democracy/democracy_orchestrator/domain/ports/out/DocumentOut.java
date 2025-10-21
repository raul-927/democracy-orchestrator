package com.democracy.democracy_orchestrator.domain.ports.out;

import com.democracy.democracy_orchestrator.domain.models.Document;
import reactor.core.publisher.Flux;

public interface DocumentOut {

    Flux<Document> selectDocument(Document document);
}

package com.democracy.democracy_orchestrator.domain.ports.in.document;

import com.democracy.democracy_orchestrator.domain.models.Document;
import reactor.core.publisher.Flux;

public interface SelectDocumentIn {

    Flux<Document> selectDocument(Document document);
}

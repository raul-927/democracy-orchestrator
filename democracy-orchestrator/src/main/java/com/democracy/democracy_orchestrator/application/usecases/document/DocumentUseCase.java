package com.democracy.democracy_orchestrator.application.usecases.document;

import com.democracy.democracy_orchestrator.domain.models.Document;
import com.democracy.democracy_orchestrator.domain.ports.in.document.SelectDocumentIn;
import com.democracy.democracy_orchestrator.domain.ports.out.DocumentOut;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class DocumentUseCase implements SelectDocumentIn {

    private final DocumentOut documentOut;

    public DocumentUseCase(DocumentOut documentOut) {
        this.documentOut = documentOut;
    }

    @Override
    public Flux<Document> selectDocument(Document document) {
        return documentOut.selectDocument(document);
    }
}

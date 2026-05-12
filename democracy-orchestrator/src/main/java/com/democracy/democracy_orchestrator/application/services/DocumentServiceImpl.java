package com.democracy.democracy_orchestrator.application.services;

import com.democracy.democracy_orchestrator.domain.models.Document;
import com.democracy.democracy_orchestrator.domain.models.Person;
import com.democracy.democracy_orchestrator.domain.ports.in.document.SelectDocumentIn;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class DocumentServiceImpl implements DocumentService{

    private final SelectDocumentIn selectDocumentIn;

    public DocumentServiceImpl(SelectDocumentIn selectDocumentIn) {
        this.selectDocumentIn = selectDocumentIn;
    }

    @Override
    public Flux<Document> selectDocument(Document document) {
        return selectDocumentIn.selectDocument(document);
    }

    @Override
    public Flux<Document> selectDocumentByCedula(Person person) {
        return selectDocumentIn.selectDocumentByCedula(person);
    }
}

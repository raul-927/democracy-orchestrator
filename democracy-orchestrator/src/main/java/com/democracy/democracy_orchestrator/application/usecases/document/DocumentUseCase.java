package com.democracy.democracy_orchestrator.application.usecases.document;

import com.democracy.democracy_orchestrator.domain.models.Document;
import com.democracy.democracy_orchestrator.domain.models.Person;
import com.democracy.democracy_orchestrator.domain.ports.in.document.SelectDocumentIn;
import com.democracy.democracy_orchestrator.domain.ports.out.DocumentOut;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class DocumentUseCase implements SelectDocumentIn {

    private int count;

    private final DocumentOut documentOut;

    public DocumentUseCase(DocumentOut documentOut) {
        this.documentOut = documentOut;
    }

    @Override
    public Flux<Document> selectDocument(Document document) {
        return documentOut.selectDocument(document);
    }

    @Override
    public Flux<Document> selectDocumentByCedula(Person person) {
        Flux<Document> documentFlux = documentOut.selectDocumentByCedula(person);

        documentFlux.doOnNext(next->{
            count++;
        })
                .doOnComplete(()->{
                    System.out.println("COUNT: "+count + ", CEDULA: "+person.getCedula());
                }).subscribe();
        return documentOut.selectDocumentByCedula(person);
    }
}

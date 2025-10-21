package com.democracy.democracy_orchestrator.infrastructure.adapters;


import com.democracy.democracy_orchestrator.application.services.TokenService;
import com.democracy.democracy_orchestrator.domain.models.CriminalRecord;
import com.democracy.democracy_orchestrator.domain.models.Department;
import com.democracy.democracy_orchestrator.domain.ports.out.CriminalRecordOut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
public class CriminalRecordAdapter implements CriminalRecordOut {
    @Autowired
    private WebClient webClient;

    @Autowired
    private TokenService tokenService;


    @Override
    public Flux<CriminalRecord> selectCriminalRecord(CriminalRecord criminalRecord) {
        BodyInserter<CriminalRecord, ReactiveHttpOutputMessage> selectCriminalRecord = BodyInserters.fromValue(criminalRecord);
        return webClient.post()
                .uri("http://localhost:8082/humanresources/criminalrecord/select")
                .headers((headers) -> headers.add("authorization", tokenService.obtainToken()))
                .body(selectCriminalRecord)
                .retrieve()
                .bodyToFlux(CriminalRecord.class);
    }
}

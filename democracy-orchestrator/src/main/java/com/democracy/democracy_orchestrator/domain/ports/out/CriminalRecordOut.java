package com.democracy.democracy_orchestrator.domain.ports.out;

import com.democracy.democracy_orchestrator.domain.models.CriminalRecord;
import reactor.core.publisher.Flux;

public interface CriminalRecordOut {

    Flux<CriminalRecord> selectCriminalRecord(CriminalRecord criminalRecord);
}

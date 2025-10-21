package com.democracy.democracy_orchestrator.domain.ports.in.criminalrecord;

import com.democracy.democracy_orchestrator.domain.models.CriminalRecord;
import reactor.core.publisher.Flux;

public interface SelectCriminalRecordIn {

    Flux<CriminalRecord> selectCriminalRecord(CriminalRecord criminalRecord);
}

package com.democracy.democracy_orchestrator.domain.models;

import lombok.*;
import lombok.experimental.Accessors;
import java.util.List;

@ToString
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Investigation {
    private int 				  id;
    private int 				  investigationId;
    private Person                person;
    private List<CriminalRecord>  criminalRecords;
    private List<Qualification>   qualifications;
    private String 				  observation;
}

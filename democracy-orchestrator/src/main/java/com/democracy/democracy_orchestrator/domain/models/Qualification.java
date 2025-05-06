package com.democracy.democracy_orchestrator.domain.models;


import lombok.*;
import lombok.experimental.Accessors;

@ToString
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Qualification {
    private int 			id;
    private String 			qualificationId;
    private Person          person;
    private Institute 		institute;
    private Document        document;
    private boolean 		verified;
    private boolean			approved;
}

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
public class Institute {

    private String 		   instituteId;
    private String 		   instituteName;
    private Address 	   address;

}

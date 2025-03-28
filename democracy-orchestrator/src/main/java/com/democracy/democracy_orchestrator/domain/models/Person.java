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
public class Person {

    private int 	   			id;
    private int 	   			personId;
    private int 	   			cedula;
    private int					civicCredential;
    private String 	   			firstName;
    private String 	   			secondName;
    private String 	   			firstLastName;
    private String 	   			secondLastName;
    private Address    			address;
    private Profession 			profession;
}

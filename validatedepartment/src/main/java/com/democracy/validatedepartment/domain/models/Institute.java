package com.democracy.validatedepartment.domain.models;


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

    private int 		   instituteId;
    private String 		   instituteName;
    private Address 	   address;

}

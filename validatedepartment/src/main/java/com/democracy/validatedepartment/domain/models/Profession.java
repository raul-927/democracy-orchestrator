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
public class Profession {

    private int 	id;
    private int 	professionId;
    private String 	professionName;
}

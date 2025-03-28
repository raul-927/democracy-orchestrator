package com.democracy.validatedepartment.domain.models;



import com.democracy.validatedepartment.domain.enums.StreetType;
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
public class Street {
    private String 		streetId;
    private String 		streetName;
    private StreetType streetType;

}

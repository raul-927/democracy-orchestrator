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
public class Address {
    private int 			id;
    private String 			addressId;
    private String 			geoLocation;
    private String 		    addressNumber;
    private Department      department;
    private City            city;
    private Neighborhood    neighborhood;
    private Street          street1;
    private Street          street2;

}

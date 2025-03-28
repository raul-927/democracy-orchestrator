package com.democracy.validatedepartment.domain.models;


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
public class City {
    private String cityId;
    private String cityName;
    private List<Neighborhood> neighborhoodList;
}

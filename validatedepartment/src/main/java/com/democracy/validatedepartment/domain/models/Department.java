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
public class Department {

    private int 	id;
    private String 	departmentId;
    private String 	departmentName;
    private List<City> cityList;
}

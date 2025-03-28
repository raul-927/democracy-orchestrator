package demo.domain;


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
public class Neighborhood {

    private int 		id;
    private String 		neighborhoodId;
    private String 		neighborhoodName;
    private List<Street>    streets;
}

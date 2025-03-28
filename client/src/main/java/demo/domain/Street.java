package demo.domain;


import demo.enums.StreetType;
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

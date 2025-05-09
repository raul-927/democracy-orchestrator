package demo.domain;


import demo.enums.OrderType;
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
public class Order {
    private Integer orderId;
    private Product product;
    private OrderType orderType;
}

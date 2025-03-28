package com.democracy.validatedepartment.domain.models;

import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@ToString
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class OrderInvoice {

    private  Long id;
    private LocalDate localDate;
    private String state;
    private String event;
    private String paymentType;
}

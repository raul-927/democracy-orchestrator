package com.democracy.democracy_orchestrator.domain.models;


import lombok.*;
import lombok.experimental.Accessors;

import java.sql.Blob;

@ToString
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Document {

    private int 		id;
    private int 		documentId;
    private String 		documentName;
    private boolean 	verified;
    private boolean		approved;
    private String 		observation;
    private Blob        attachment;
}

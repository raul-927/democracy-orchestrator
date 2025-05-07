package com.democracy.democracy_orchestrator.domain.models;


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
public class Document {

    private int 		id;
    private String 		documentId;
    private String 		documentName;
    private boolean 	documentVerified;
    private boolean		documentApproved;
    private String 		documentObservation;
    private byte[]        documentAttachment;
}

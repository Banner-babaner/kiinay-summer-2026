package dev.vorstu.dto.common;

import lombok.*;

@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class VeryPrimitiveMessage {
    @Getter
    private String to;
    @Getter
    private String subject;
    @Getter
    private String text;
}

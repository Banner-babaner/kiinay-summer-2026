package dev.vorstu.dto.event;


import io.swagger.v3.oas.models.security.SecurityScheme;

import java.time.LocalDateTime;

public record EmailErrorEvent(
        String to,
        String subject,
        String text,
        String errorMessage,
        LocalDateTime timestamp,
        Integer retryNumber
) {}
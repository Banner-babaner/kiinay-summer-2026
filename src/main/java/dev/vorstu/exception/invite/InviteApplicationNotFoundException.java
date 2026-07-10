package dev.vorstu.exception.invite;

import jakarta.persistence.EntityNotFoundException;

public class InviteApplicationNotFoundException extends EntityNotFoundException {
    public InviteApplicationNotFoundException(String message) {
        super(message);
    }
}

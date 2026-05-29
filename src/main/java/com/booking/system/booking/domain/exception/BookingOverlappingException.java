package com.booking.system.booking.domain.exception;

public class BookingOverlappingException extends DomainException {
    public BookingOverlappingException(String message) {
        super(message);
    }
}

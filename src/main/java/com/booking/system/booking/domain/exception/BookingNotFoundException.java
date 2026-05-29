package com.booking.system.booking.domain.exception;

public class BookingNotFoundException extends DomainException {
    public BookingNotFoundException(String message) {
        super(message);
    }
}

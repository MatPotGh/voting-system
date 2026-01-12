package com.example.voting.service.exceptions;

public class PeselAlreadyExistsException extends RuntimeException {
    public PeselAlreadyExistsException(String pesel) {
        super("Voter with PESEL='" + pesel + "' already exists");
    }
}

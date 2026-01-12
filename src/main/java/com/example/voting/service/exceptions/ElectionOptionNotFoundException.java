package com.example.voting.service.exceptions;

public class ElectionOptionNotFoundException extends EntityNotFoundException {
    public ElectionOptionNotFoundException(Long id) {
        super("ElectionOption", id);
    }
}


package com.example.voting.service.exceptions;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entityName, Object id) {
        super(entityName + " with id=" + id + " not found");
    }
}


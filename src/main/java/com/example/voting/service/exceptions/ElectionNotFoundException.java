package com.example.voting.service.exceptions;

public class ElectionNotFoundException extends EntityNotFoundException {
  public ElectionNotFoundException(Long id) {
    super("Election", id);
  }
}


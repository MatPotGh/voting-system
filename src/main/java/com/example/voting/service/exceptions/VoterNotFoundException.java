package com.example.voting.service.exceptions;

public class VoterNotFoundException extends EntityNotFoundException {
  public VoterNotFoundException(Long id) {
    super("Voter", id);
  }
}


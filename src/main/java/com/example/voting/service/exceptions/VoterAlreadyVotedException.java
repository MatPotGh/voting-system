package com.example.voting.service.exceptions;

public class VoterAlreadyVotedException extends RuntimeException {
    public VoterAlreadyVotedException(Long voterId, Long electionId) {
        super("Voter with id=" + voterId + " has already voted in election id=" + electionId);
    }
}


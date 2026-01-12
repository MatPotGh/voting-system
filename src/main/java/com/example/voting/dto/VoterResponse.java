package com.example.voting.dto;

public record VoterResponse(
    Long id,
    String pesel,
    boolean blocked
) {
}

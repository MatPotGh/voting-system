package com.example.voting.internal.dto;

public record ElectionOptionResponse(
    Long id,
    String candidateFirstName,
    String candidateLastName
) {
}

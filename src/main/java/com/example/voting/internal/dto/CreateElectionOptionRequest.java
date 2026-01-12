package com.example.voting.internal.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateElectionOptionRequest(
    @NotBlank String firstName,
    @NotBlank String lastName,
    @Min(18) int age
) {
}

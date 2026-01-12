package com.example.voting.internal.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateElectionRequest(
        @NotBlank String name
) {}

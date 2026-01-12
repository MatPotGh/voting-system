package com.example.voting.dto;

import jakarta.validation.constraints.NotNull;

public record VoteRequest(
    @NotNull Long voterId,
    @NotNull Long optionId
) {}

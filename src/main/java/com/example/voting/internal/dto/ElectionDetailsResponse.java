package com.example.voting.internal.dto;

import java.util.List;

public record ElectionDetailsResponse(
    Long id,
    String name,
    List<ElectionOptionResponse> options
) {}

package com.example.voting.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateVoterRequest(
    @NotBlank
    @Pattern(regexp = "\\d{11}", message = "PESEL must consist of exactly 11 digits")
    String pesel
) {
}

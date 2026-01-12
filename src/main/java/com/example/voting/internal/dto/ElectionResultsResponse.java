package com.example.voting.internal.dto;

import java.util.List;

public record ElectionResultsResponse(
    String electionName,
    List<ElectionOptionResultResponse> options
) {
  public record ElectionOptionResultResponse(
      String candidateFirstName,
      String candidateLastName,
      Long voteCount
  ) {
  }
}

package com.example.voting.controller;

import com.example.voting.dto.VoteRequest;
import com.example.voting.internal.dto.ElectionDetailsResponse;
import com.example.voting.internal.dto.ElectionSummaryResponse;
import com.example.voting.service.PublicElectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/elections")
@RequiredArgsConstructor
@Tag(name = "Public Elections", description = "Public API for elections and voting")
public class PublicElectionController {

  private final PublicElectionService publicElectionService;

  @GetMapping
  @Operation(summary = "List elections", description = "Returns all active elections")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "OK")
  })
  public List<ElectionSummaryResponse> getAll() {
    return publicElectionService.getAllElections();
  }

  @GetMapping("/{id}")
  @Operation(summary = "Election details", description = "Returns details for the given election ID")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "OK"),
      @ApiResponse(responseCode = "404", description = "Election not found")
  })
  public ElectionDetailsResponse getDetails(@PathVariable("id") Long id) {
    return publicElectionService.getElectionDetails(id);
  }

  @PostMapping("/{id}/votes")
  @Operation(summary = "Cast vote", description = "Casts a vote in election with the given ID for the specified voter")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Vote cast"),
      @ApiResponse(responseCode = "404", description = "Election or resource not found"),
      @ApiResponse(responseCode = "409", description = "Voter has already voted in this election"),
      @ApiResponse(responseCode = "400", description = "Invalid data or operation")
  })
  public ResponseEntity<Void> castVote(@PathVariable("id") Long id, @Valid @RequestBody VoteRequest request) {
    publicElectionService.castVote(id, request.voterId(), request.optionId());
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }
}

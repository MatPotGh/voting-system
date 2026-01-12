package com.example.voting.internal.controller;

import com.example.voting.internal.dto.CreateElectionOptionRequest;
import com.example.voting.internal.dto.CreateElectionRequest;
import com.example.voting.internal.dto.ElectionResultsResponse;
import com.example.voting.internal.dto.ElectionOptionResponse;
import com.example.voting.internal.dto.ElectionResponse;
import com.example.voting.internal.service.ElectionService;
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

@RestController
@RequestMapping("/api/admin/elections")
@RequiredArgsConstructor
@Tag(name = "Elections", description = "Admin management of elections")
public class ElectionController {

  private final ElectionService electionService;

  @PostMapping
  @Operation(summary = "Create election", description = "Creates a new election")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Election created"),
      @ApiResponse(responseCode = "400", description = "Invalid input")
  })
  public ResponseEntity<ElectionResponse> create(@Valid @RequestBody CreateElectionRequest request) {
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(electionService.createElection(request));
  }

  @PostMapping("/{electionId}/options")
  @Operation(summary = "Add option to election", description = "Adds a new option to the given election")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Option added"),
      @ApiResponse(responseCode = "404", description = "Election not found"),
      @ApiResponse(responseCode = "400", description = "Invalid input")
  })
  public ResponseEntity<ElectionOptionResponse> addOption(
      @PathVariable("electionId") Long electionId,
      @Valid @RequestBody CreateElectionOptionRequest request) {

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(electionService.addOption(electionId, request));
  }

  @GetMapping("/{electionId}/results")
  @Operation(summary = "Get election results", description = "Returns current results for given election (admin only)")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Results returned"),
      @ApiResponse(responseCode = "404", description = "Election not found")
  })
  public ResponseEntity<ElectionResultsResponse> getResults(@PathVariable("electionId") Long electionId) {
    return ResponseEntity.ok(electionService.getElectionResults(electionId));
  }
}

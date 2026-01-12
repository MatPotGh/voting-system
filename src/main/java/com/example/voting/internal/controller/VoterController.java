package com.example.voting.internal.controller;

import com.example.voting.dto.CreateVoterRequest;
import com.example.voting.dto.VoterResponse;
import com.example.voting.internal.service.VoterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/voters")
@Tag(name = "Voters", description = "Admin management of voters")
public class VoterController {

  private final VoterService voterService;

  public VoterController(VoterService voterService) {
    this.voterService = voterService;
  }

  @PostMapping
  @Operation(summary = "Create voter", description = "Creates a new voter")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Voter created"),
      @ApiResponse(responseCode = "409", description = "Voter with given PESEL already exists"),
      @ApiResponse(responseCode = "400", description = "Invalid input")
  })
  public ResponseEntity<VoterResponse> create(@Valid @RequestBody CreateVoterRequest request) {
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(voterService.createVoter(request));
  }

  @PatchMapping("/{id}/block")
  @Operation(summary = "Block voter", description = "Blocks voting for the given voter ID")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Voter blocked"),
      @ApiResponse(responseCode = "404", description = "Voter not found"),
      @ApiResponse(responseCode = "400", description = "Invalid operation")
  })
  public ResponseEntity<Void> block(@PathVariable("id") Long id) {
    voterService.block(id);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{id}/unblock")
  @Operation(summary = "Unblock voter", description = "Unblocks voting for the given voter ID")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Voter unblocked"),
      @ApiResponse(responseCode = "404", description = "Voter not found"),
      @ApiResponse(responseCode = "400", description = "Invalid operation")
  })
  public ResponseEntity<Void> unblock(@PathVariable("id") Long id) {
    voterService.unblock(id);
    return ResponseEntity.noContent().build();
  }
}

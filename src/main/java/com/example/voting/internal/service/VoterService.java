package com.example.voting.internal.service;

import com.example.voting.dto.CreateVoterRequest;
import com.example.voting.dto.VoterResponse;

public interface VoterService {
  VoterResponse createVoter(CreateVoterRequest request);
  void block(Long id);
  void unblock(Long id);
}

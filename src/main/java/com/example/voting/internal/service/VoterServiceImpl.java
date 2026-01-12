package com.example.voting.internal.service;

import com.example.voting.domain.Voter;
import com.example.voting.dto.CreateVoterRequest;
import com.example.voting.dto.VoterResponse;
import com.example.voting.repository.VoterRepository;
import com.example.voting.service.exceptions.PeselAlreadyExistsException;
import com.example.voting.service.exceptions.VoterNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class VoterServiceImpl implements VoterService {

  private final VoterRepository voterRepository;

  @Override
  @Transactional
  public VoterResponse createVoter(CreateVoterRequest request) {
    voterRepository.findByPesel(request.pesel()).ifPresent(v -> {
      throw new PeselAlreadyExistsException(request.pesel());
    });
    var saved = voterRepository.save(
        Voter.builder()
            .pesel(request.pesel())
            .blocked(false)
            .createdAt(OffsetDateTime.now())
            .build()
    );
    return new VoterResponse(saved.getId(), saved.getPesel(), saved.isBlocked());
  }

  @Override
  @Transactional
  public void block(Long id) {
    var voter = voterRepository.findById(id)
        .orElseThrow(() -> new VoterNotFoundException(id));
    voter.setBlocked(true);
  }

  @Override
  @Transactional
  public void unblock(Long id) {
    var voter = voterRepository.findById(id)
        .orElseThrow(() -> new VoterNotFoundException(id));
    voter.setBlocked(false);
  }
}


package com.example.voting.repository;

import com.example.voting.domain.Voter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoterRepository extends JpaRepository<Voter, Long> {
  Optional<Voter> findByPesel(String pesel);
}

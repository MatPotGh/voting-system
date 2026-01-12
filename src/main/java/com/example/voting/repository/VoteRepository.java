package com.example.voting.repository;

import com.example.voting.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
  boolean existsByVoterIdAndElectionId(Long voterId, Long electionId);
  boolean existsByVoterIdAndOptionId(Long voterId, Long optionId);
  @Query("SELECT COUNT(v) FROM Vote v WHERE v.option.id = :optionId")
  Long countByOptionId(@Param("optionId") Long optionId);
}

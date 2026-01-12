package com.example.voting.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "election_options")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ElectionOption {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "election_id", nullable = false)
  private Election election;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "candidate_id", nullable = false)
  private Candidate candidate;
}

package com.example.voting.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "voters")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Voter {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String pesel;

  @Column(nullable = false)
  private boolean blocked;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;
}


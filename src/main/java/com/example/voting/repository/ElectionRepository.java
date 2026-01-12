package com.example.voting.repository;

import com.example.voting.domain.Election;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@Repository
public interface ElectionRepository extends JpaRepository<Election, Long> {
  @Query("select e from Election e left join fetch e.options where e.id = :id")
  Optional<Election> findByIdWithOptions(@Param("id") Long id);
}


package com.example.voting.repository;

import com.example.voting.domain.ElectionOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElectionOptionRepository extends JpaRepository<ElectionOption, Long> {
}

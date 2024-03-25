package com.fastcampus.loan.repository;

import com.fastcampus.loan.domain.Entry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.Optional;

public interface EntryRepository extends JpaRepository<Entry, Long> {

    Optional<Entry> findByApplicationId(Long applicationId);
}

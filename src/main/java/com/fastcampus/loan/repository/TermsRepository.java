package com.fastcampus.loan.repository;

import com.fastcampus.loan.domain.Terms;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TermsRepository extends JpaRepository<Terms, Long> {
}

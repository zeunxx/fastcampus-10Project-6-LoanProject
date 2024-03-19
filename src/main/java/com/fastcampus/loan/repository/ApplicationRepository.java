package com.fastcampus.loan.repository;

import com.fastcampus.loan.domain.Application;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
}

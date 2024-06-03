package com.backend.dorandoran.assessment.repository;

import com.backend.dorandoran.user.domain.entity.UserMentalState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMentalStateRepository extends JpaRepository<UserMentalState, Long> {

    Boolean existsByUserId(Long userId);
}
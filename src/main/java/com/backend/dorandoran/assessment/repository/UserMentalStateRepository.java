package com.backend.dorandoran.assessment.repository;

import com.backend.dorandoran.user.domain.entity.UserMentalState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserMentalStateRepository extends JpaRepository<UserMentalState, Long> {

    Optional<UserMentalState> findByUserId(Long userId);
}

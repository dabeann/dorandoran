package com.backend.dorandoran.assessment.repository;

import com.backend.dorandoran.user.domain.entity.User;
import com.backend.dorandoran.user.domain.entity.UserMentalState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserMentalStateRepository extends JpaRepository<UserMentalState, Long> {

    Boolean existsByUserId(Long userId);

    Optional<UserMentalState> findFirstByUserOrderByCreatedDateTimeDesc(User user);

    void deleteByUserId(Long userId);
}
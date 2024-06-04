package com.backend.dorandoran.assessment.repository;

import com.backend.dorandoran.user.domain.entity.User;
import com.backend.dorandoran.user.domain.entity.UserMentalState;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMentalStateRepository extends JpaRepository<UserMentalState, Long> {

    Boolean existsByUserId(Long userId);

    Optional<UserMentalState> findFirstByUserOrderByCreatedDateTimeDesc(User user);
}
package com.backend.dorandoran.assessment.repository;

import com.backend.dorandoran.user.domain.entity.User;
import com.backend.dorandoran.user.domain.entity.UserMentalState;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserMentalStateRepository extends JpaRepository<UserMentalState, Long> {

    Boolean existsByUserId(Long userId);

    Optional<UserMentalState> findFirstByUserOrderByCreatedDateTimeDesc(User user);

    @Modifying
    @Transactional
    @Query(value = "delete from user_mental_state where user_id = :userId", nativeQuery = true)
    void deleteByUserId(@Param("userId") Long userId);
}
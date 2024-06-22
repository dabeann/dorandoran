package com.backend.dorandoran.user.repository;

import com.backend.dorandoran.user.domain.entity.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, Long> {

    Optional<UserToken> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}

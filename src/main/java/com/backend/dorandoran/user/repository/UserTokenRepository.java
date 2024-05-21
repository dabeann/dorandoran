package com.backend.dorandoran.user.repository;

import com.backend.dorandoran.user.domain.entity.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, Long> {

    UserToken findByUserId(Long userId);
}

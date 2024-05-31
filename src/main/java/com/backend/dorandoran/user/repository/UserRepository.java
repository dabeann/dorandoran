package com.backend.dorandoran.user.repository;

import com.backend.dorandoran.user.domain.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.name = :name or u.phoneNumber = :phoneNumber")
    Optional<User> findByNameAndPhoneNumber(@Param("name") String name, @Param("phoneNumber") String phoneNumber);

    List<User> findAllByDiseasesNotNull();
}
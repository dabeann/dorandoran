package com.backend.dorandoran.counsel.repository;

import com.backend.dorandoran.counsel.domain.entity.Counsel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CounselRepository extends JpaRepository<Counsel, Long> {
}

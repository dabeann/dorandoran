package com.backend.dorandoran.counsel.repository;

import com.backend.dorandoran.counsel.domain.entity.Dialog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DialogRepository extends JpaRepository<Dialog, Long> {
}

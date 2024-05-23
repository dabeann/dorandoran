package com.backend.dorandoran.counsel.repository;

import com.backend.dorandoran.common.domain.DialogRole;
import com.backend.dorandoran.counsel.domain.entity.Counsel;
import com.backend.dorandoran.counsel.domain.entity.Dialog;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DialogRepository extends JpaRepository<Dialog, Long> {
    Optional<Dialog> findFirstByCounselAndRoleOrderByCreatedDateTimeDesc(Counsel counselId, DialogRole role);
}

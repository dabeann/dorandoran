package com.backend.dorandoran.counsel.repository;

import com.backend.dorandoran.common.domain.dialog.DialogRole;
import com.backend.dorandoran.counsel.domain.entity.Counsel;
import com.backend.dorandoran.counsel.domain.entity.Dialog;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DialogRepository extends JpaRepository<Dialog, Long> {
    List<Dialog> findAllByCounselOrderByCreatedDateTimeAsc(Counsel counsel);

    boolean existsByCounselAndRole(Counsel counsel, DialogRole dialogRole);

    List<Dialog> findAllByCounselOrderByCreatedDateTimeAsc(Counsel counsel, Pageable pageable);
}

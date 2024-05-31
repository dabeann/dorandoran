package com.backend.dorandoran.counsel.repository;

import com.backend.dorandoran.common.domain.counsel.CounselState;
import com.backend.dorandoran.counsel.domain.entity.Counsel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CounselRepository extends JpaRepository<Counsel, Long> {

    List<Counsel> findAllByStateOrderByCreatedDateTimeDesc(CounselState state);
}

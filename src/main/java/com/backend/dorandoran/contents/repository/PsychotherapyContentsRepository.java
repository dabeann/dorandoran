package com.backend.dorandoran.contents.repository;

import com.backend.dorandoran.common.domain.Disease;
import com.backend.dorandoran.contents.domain.entity.PsychotherapyContents;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PsychotherapyContentsRepository extends JpaRepository<PsychotherapyContents, Long> {

    List<PsychotherapyContents> findAllByCategory(Disease disease);
}

package com.backend.dorandoran.contents.repository;

import com.backend.dorandoran.common.domain.MeditationDuration;
import com.backend.dorandoran.contents.domain.entity.MeditationContents;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeditationContentsRepository extends JpaRepository<MeditationContents, Long> {
    MeditationContents findFirstByDurationCategory(MeditationDuration duration);
}

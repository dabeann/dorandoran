package com.backend.dorandoran.counsel.repository;

import com.backend.dorandoran.common.domain.counsel.CounselState;
import com.backend.dorandoran.counsel.domain.entity.Counsel;
import com.backend.dorandoran.user.domain.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CounselRepository extends JpaRepository<Counsel, Long> {

    List<Counsel> findAllByUser(User user);

    Long countByTitleAndUser(String title, User user);
}

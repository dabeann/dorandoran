package com.backend.dorandoran.contents.repository;

import com.backend.dorandoran.common.domain.Disease;
import com.backend.dorandoran.contents.domain.entity.Quotation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuotationRepository extends JpaRepository<Quotation, Long> {

    List<Quotation> findAllByCategoryIn(List<Disease> categories);
}

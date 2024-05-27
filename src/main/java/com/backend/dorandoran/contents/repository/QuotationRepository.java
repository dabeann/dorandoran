package com.backend.dorandoran.contents.repository;

import com.backend.dorandoran.contents.domain.entity.Quotation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuotationRepository extends JpaRepository<Quotation, Long> {
}

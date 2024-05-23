package com.backend.dorandoran.counsel.domain.entity;

import com.backend.dorandoran.common.domain.BaseDateTimeEntity;
import com.backend.dorandoran.common.domain.DialogRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "dialog")
@Entity
public class Dialog extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dialog_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counsel_id")
    private Counsel counsel;

    @Column(name = "contents", nullable = false, length = 1024)
    private String contents;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private DialogRole role;
}

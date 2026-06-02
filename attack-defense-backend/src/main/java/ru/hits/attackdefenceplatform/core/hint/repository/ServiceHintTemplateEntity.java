package ru.hits.attackdefenceplatform.core.hint.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import ru.hits.attackdefenceplatform.core.vulnerable_service.repository.VulnerableServiceEntity;

import java.util.UUID;

@Entity
@Table(name = "service_hint_templates")
@Data
public class ServiceHintTemplateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "service_id", nullable = false)
    private VulnerableServiceEntity service;

    @Column(name = "hint_level", nullable = false)
    private int level;

    @Column(name = "hint_text", nullable = false)
    private String text;

    @Column(name = "multiplier", nullable = false)
    private double multiplier;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;
}


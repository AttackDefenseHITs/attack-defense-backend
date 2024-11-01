package ru.hits.attackdefenceplatform.core.team.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TeamRepository extends JpaRepository<TeamEntity, UUID> { }

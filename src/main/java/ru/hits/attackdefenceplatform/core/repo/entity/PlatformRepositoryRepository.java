package ru.hits.attackdefenceplatform.core.repo.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlatformRepositoryRepository extends JpaRepository<PlatformRepository, Long> {
    Optional<PlatformRepository> findTopByOrderByCreatedAtDesc();
}

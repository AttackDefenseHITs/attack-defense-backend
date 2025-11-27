package ru.hits.attackdefenceplatform.modules.repo.model;

import java.time.LocalDateTime;

public record RepositoryInfoDto(
        String name,
        String fullName,
        String htmlUrl,
        String defaultBranch,
        boolean isPrivate,
        String lastCommitSha,
        LocalDateTime createdAt,
        String type
) {}
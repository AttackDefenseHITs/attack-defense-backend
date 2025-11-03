package ru.hits.attackdefenceplatform.repo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "platform_repository")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlatformRepository {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Полное имя репозитория, например:
     * AttackDefenseHITs/attack-defense-template
     */
    @Column(nullable = false, unique = true)
    private String fullName;

    /**
     * URL для быстрого доступа в интерфейсе.
     * Например: https://github.com/AttackDefenseHITs/attack-defense-template
     */
    @Column(nullable = false)
    private String url;

    /**
     * Ветка, с которой работает платформа (по умолчанию main).
     */
    @Column(nullable = false)
    private String branch = "main";

    /**
     * Тип хранилища: GITHUB, LOCAL, GITLAB и т.д.
     */
    @Column(nullable = false)
    private String type;

    /**
     * Последний известный SHA последнего коммита.
     * Используется для проверки необходимости синхронизации.
     */
    private String lastCommitSha;

    /**
     * Приватный ли репозиторий.
     */
    private boolean isPrivate;

    /**
     * Время последней синхронизации (когда мы обновляли данные в БД).
     */
    private LocalDateTime lastSyncedAt;

    /**
     * Когда была добавлена запись (для аудита).
     */
    private LocalDateTime createdAt;
}

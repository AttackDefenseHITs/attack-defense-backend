package ru.hits.attackdefenceplatform.modules.repo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.attackdefenceplatform.modules.repo.entity.PlatformRepository;
import ru.hits.attackdefenceplatform.modules.repo.entity.PlatformRepositoryRepository;
import ru.hits.attackdefenceplatform.modules.repo.model.RepositoryInfoDto;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RepositoryService {
    private final RepositoryAdapter repositoryAdapter;
    private final PlatformRepositoryRepository platformRepositoryRepository;

    /**
     * Создаёт новый репозиторий с шаблонной структурой
     * и сохраняет информацию о нём в базе данных.
     */
    @Transactional
    public RepositoryInfoDto createRepositoryWithTemplate() {
        var repositoryDto = repositoryAdapter.createRepositoryWithTemplate();

        var entity = PlatformRepository.builder()
                .fullName(repositoryDto.fullName())
                .url(repositoryDto.htmlUrl())
                .branch(repositoryDto.defaultBranch())
                .type(repositoryDto.type())
                .isPrivate(repositoryDto.isPrivate())
                .lastCommitSha(repositoryDto.lastCommitSha())
                .createdAt(repositoryDto.createdAt())
                .lastSyncedAt(LocalDateTime.now())
                .build();

        platformRepositoryRepository.save(entity);

        return repositoryDto;
    }

    /**
     * Отдает информацию о текущем репозитории платформы из базы данных
     */
    @Transactional(readOnly = true)
    public RepositoryInfoDto getCurrentRepositoryFromDB() {
        PlatformRepository repo = platformRepositoryRepository.findTopByOrderByCreatedAtDesc()
                .orElseThrow(() -> new IllegalStateException("No platform repository configured"));

        return new RepositoryInfoDto(
                repo.getFullName().substring(repo.getFullName().indexOf("/") + 1),
                repo.getFullName(),
                repo.getUrl(),
                repo.getBranch(),
                repo.isPrivate(),
                repo.getLastCommitSha(),
                repo.getCreatedAt(),
                repo.getType()
        );
    }

    @Transactional
    public void updateLastCommit(String lastCommitSha) {
        PlatformRepository repo = platformRepositoryRepository.findTopByOrderByCreatedAtDesc()
                .orElseThrow(() -> new IllegalStateException("No platform repository configured"));
        repo.setLastCommitSha(lastCommitSha);
        platformRepositoryRepository.save(repo);
    }
}

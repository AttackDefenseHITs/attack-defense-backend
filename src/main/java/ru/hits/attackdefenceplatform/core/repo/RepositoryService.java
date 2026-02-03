package ru.hits.attackdefenceplatform.core.repo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.attackdefenceplatform.core.repo.entity.PlatformRepository;
import ru.hits.attackdefenceplatform.core.repo.entity.PlatformRepositoryRepository;
import ru.hits.attackdefenceplatform.core.repo.model.RepositoryInfoDto;

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
        return platformRepositoryRepository.findTopByOrderByCreatedAtDesc()
                .map(repo -> new RepositoryInfoDto(
                        repo.getFullName().substring(repo.getFullName().indexOf("/") + 1),
                        repo.getFullName(),
                        repo.getUrl(),
                        repo.getBranch(),
                        repo.isPrivate(),
                        repo.getLastCommitSha(),
                        repo.getCreatedAt(),
                        repo.getType()
                ))
                .orElseGet(RepositoryService::emptyRepositoryInfo);
    }

    @Transactional
    public void updateLastCommit(String lastCommitSha) {
        platformRepositoryRepository.findTopByOrderByCreatedAtDesc().ifPresent(repo -> {
            repo.setLastCommitSha(lastCommitSha);
            platformRepositoryRepository.save(repo);
        });
    }

    private static RepositoryInfoDto emptyRepositoryInfo() {
        return new RepositoryInfoDto(
                "",         // name
                "",         // fullName
                "",         // htmlUrl
                "",         // branch
                false,      // isPrivate
                "",         // lastCommitSha
                null,       // createdAt
                null        // type (если enum/строка — ок)
        );
    }
}

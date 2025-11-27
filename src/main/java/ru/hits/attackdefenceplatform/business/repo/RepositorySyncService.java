package ru.hits.attackdefenceplatform.business.repo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.attackdefenceplatform.business.checker.CheckerManagementService;
import ru.hits.attackdefenceplatform.business.repo.detector.CheckerDetector;
import ru.hits.attackdefenceplatform.business.repo.detector.ServiceDetector;
import ru.hits.attackdefenceplatform.business.vulnerable_service.VulnerableServiceManagementService;
import ru.hits.attackdefenceplatform.business.repo.model.RepoFileDto;
import ru.hits.attackdefenceplatform.business.repo.model.RepositoryInfoDto;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class RepositorySyncService {

    private final RepositoryAdapter repositoryAdapter;
    private final RepositoryService repositoryService;

    private final CheckerManagementService checkerManagementService;
    private final VulnerableServiceManagementService vulnerableServiceManagementService;

    private final ServiceDetector serviceDetector;
    private final CheckerDetector checkerDetector;

    /**
     * Основная точка синхронизации репозитория платформы.
     */
    @Transactional
    public void syncRepo() throws IOException {

        RepositoryInfoDto repoInfo = repositoryService.getCurrentRepositoryFromDB();
        var repoData = repositoryAdapter.getRepositoryInfo(repoInfo.fullName());

        if (Objects.equals(repoInfo.lastCommitSha(), repoData.lastCommitSha())) {
            log.info("Синхронизация не требуется — коммиты совпадают");
            return;
        }

        List<RepoFileDto> files = repositoryAdapter.getRepositoryTree(repoInfo.fullName(), repoInfo.defaultBranch());

        var services = serviceDetector.detect(files);
        vulnerableServiceManagementService.syncServices(services);

        var checkers = checkerDetector.detect(files);
        checkerManagementService.syncCheckers(
                repoInfo.fullName(),
                repoInfo.defaultBranch(),
                checkers
        );

        repositoryService.updateLastCommit(repoData.lastCommitSha());
    }
}




package ru.hits.attackdefenceplatform.core.repo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.hits.attackdefenceplatform.core.checker.CheckerManagementService;
import ru.hits.attackdefenceplatform.core.exploit.ExploitManagementService;
import ru.hits.attackdefenceplatform.core.repo.detector.CheckerDetector;
import ru.hits.attackdefenceplatform.core.repo.detector.ExploitDetector;
import ru.hits.attackdefenceplatform.core.repo.detector.ServiceDetector;
import ru.hits.attackdefenceplatform.core.vulnerable_service.VulnerableServiceManagementService;
import ru.hits.attackdefenceplatform.core.repo.model.RepoFileDto;
import ru.hits.attackdefenceplatform.core.repo.model.RepositoryInfoDto;

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
    private final ExploitManagementService exploitManagementService;

    private final ServiceDetector serviceDetector;
    private final CheckerDetector checkerDetector;
    private final ExploitDetector exploitDetector;

    /**
     * Основная точка синхронизации репозитория платформы.
     */
    @Transactional
    public void syncRepo() throws IOException {

        RepositoryInfoDto repoInfo = repositoryService.getCurrentRepositoryFromDB();
        if (repoInfo == null || !StringUtils.hasText(repoInfo.name())) {
            log.error("Репозиторий для синхронизации не найден");
            return;
        }
        var repoData = repositoryAdapter.getRepositoryInfo(repoInfo.fullName());

//        if (Objects.equals(repoInfo.lastCommitSha(), repoData.lastCommitSha())) {
//            log.info("Синхронизация не требуется — коммиты совпадают");
//            return;
//        }

        List<RepoFileDto> files = repositoryAdapter.getRepositoryTree(repoInfo.fullName(), repoInfo.defaultBranch());

        var services = serviceDetector.detect(files);
        vulnerableServiceManagementService.syncServices(services, repoInfo.htmlUrl());

        var checkers = checkerDetector.detect(files);
        checkerManagementService.syncCheckers(
                repoInfo.fullName(),
                repoInfo.defaultBranch(),
                checkers
        );

        var exploits = exploitDetector.detect(files);
        exploitManagementService.syncExploits(
                repoInfo.fullName(),
                repoInfo.defaultBranch(),
                exploits
        );

        repositoryService.updateLastCommit(repoData.lastCommitSha());
    }
}




package ru.hits.attackdefenceplatform.modules.checker;

import ru.hits.attackdefenceplatform.modules.repo.model.RepoFileDto;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CheckerManagementService {
    void uploadChecker(String scriptText, UUID serviceId) throws IOException;
    void syncCheckersFromRepository(
            String repoFullName,
            String branch,
            Map<String, List<RepoFileDto>> checkers) throws IOException;
    String getCheckerScript(UUID serviceId) throws IOException;
}

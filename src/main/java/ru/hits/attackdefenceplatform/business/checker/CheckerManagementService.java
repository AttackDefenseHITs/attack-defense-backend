package ru.hits.attackdefenceplatform.business.checker;

import ru.hits.attackdefenceplatform.business.repo.model.RepoFileDto;
import ru.hits.attackdefenceplatform.public_interface.checker.FileNodeDto;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CheckerManagementService {
    void uploadChecker(String scriptText, UUID serviceId) throws IOException;
    void syncCheckers(
            String repoFullName,
            String branch,
            Map<String, List<RepoFileDto>> checkers) throws IOException;
    @Deprecated
    String getCheckerScript(UUID serviceId) throws IOException;
    List<FileNodeDto> getCheckerFileTree(UUID serviceId) throws IOException;
    String getCheckerFileContent(UUID serviceId, String relativePath) throws IOException;
}

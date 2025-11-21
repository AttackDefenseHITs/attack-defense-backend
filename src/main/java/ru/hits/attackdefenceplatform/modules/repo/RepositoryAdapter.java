package ru.hits.attackdefenceplatform.modules.repo;

import ru.hits.attackdefenceplatform.modules.repo.model.RepoFileDto;
import ru.hits.attackdefenceplatform.modules.repo.model.RepositoryInfoDto;

import java.util.List;

public interface RepositoryAdapter {
    RepositoryInfoDto createRepositoryWithTemplate();
    RepositoryInfoDto getRepositoryInfo(String repositoryName);
    List<RepoFileDto> getRepositoryTree(String fullRepoName, String branch);
    byte[] getFileContent(String repoName, String filePath, String branch);
}

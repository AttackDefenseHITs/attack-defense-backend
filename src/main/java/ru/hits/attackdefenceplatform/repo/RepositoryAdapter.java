package ru.hits.attackdefenceplatform.repo;

import ru.hits.attackdefenceplatform.repo.model.RepoFileDto;
import ru.hits.attackdefenceplatform.repo.model.RepositoryInfoDto;

import java.util.List;

public interface RepositoryAdapter {
    RepositoryInfoDto createRepositoryWithTemplate();
    RepositoryInfoDto getRepositoryInfo(String repositoryName);
    List<RepoFileDto> getRepositoryTree(String fullRepoName, String branch);
    String getFileContent(String repoName, String filePath, String branch);
}

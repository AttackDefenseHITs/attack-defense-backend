package ru.hits.attackdefenceplatform.business.repo;

import ru.hits.attackdefenceplatform.business.repo.model.RepoFileDto;
import ru.hits.attackdefenceplatform.business.repo.model.RepositoryInfoDto;

import java.util.List;

public interface RepositoryAdapter {
    RepositoryInfoDto createRepositoryWithTemplate();
    RepositoryInfoDto getRepositoryInfo(String repositoryName);
    List<RepoFileDto> getRepositoryTree(String fullRepoName, String branch);
    byte[] getFileContent(String repoName, String filePath, String branch);
}

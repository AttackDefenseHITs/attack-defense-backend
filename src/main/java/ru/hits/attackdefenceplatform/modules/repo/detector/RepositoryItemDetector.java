package ru.hits.attackdefenceplatform.modules.repo.detector;

import ru.hits.attackdefenceplatform.modules.repo.model.RepoFileDto;

import java.util.List;

public interface RepositoryItemDetector<T> {
    T detect(List<RepoFileDto> files);
}


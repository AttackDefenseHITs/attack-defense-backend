package ru.hits.attackdefenceplatform.core.repo.detector;

import ru.hits.attackdefenceplatform.core.repo.model.RepoFileDto;

import java.util.List;

public interface RepositoryItemDetector<T> {
    T detect(List<RepoFileDto> files);
}


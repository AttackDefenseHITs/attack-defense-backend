package ru.hits.attackdefenceplatform.business.repo.detector;

import ru.hits.attackdefenceplatform.business.repo.model.RepoFileDto;

import java.util.List;

public interface RepositoryItemDetector<T> {
    T detect(List<RepoFileDto> files);
}


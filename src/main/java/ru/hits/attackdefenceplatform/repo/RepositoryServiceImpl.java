package ru.hits.attackdefenceplatform.repo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.repo.model.RepositoryInfoDto;

@Service
@RequiredArgsConstructor
public class RepositoryServiceImpl implements RepositoryService {
    private final RepositoryAdapter repositoryAdapter;

    @Override
    public RepositoryInfoDto createRepositoryWithTemplate() {
        return repositoryAdapter.createRepositoryWithTemplate();
    }
}

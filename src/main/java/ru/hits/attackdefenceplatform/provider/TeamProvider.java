package ru.hits.attackdefenceplatform.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.team.repository.TeamEntity;
import ru.hits.attackdefenceplatform.core.team.repository.TeamRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamProvider extends EntityProvider<TeamEntity> {

    private final TeamRepository teamRepository;

    @Override
    protected Optional<TeamEntity> findById(UUID id) {
        return teamRepository.findById(id);
    }

    @Override
    protected String entityName() {
        return "Команда";
    }

    @Override
    protected JpaRepository<TeamEntity, UUID> getRepository() {
        return teamRepository;
    }
}

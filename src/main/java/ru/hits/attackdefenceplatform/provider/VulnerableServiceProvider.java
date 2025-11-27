package ru.hits.attackdefenceplatform.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.modules.vulnerable_service.repository.VulnerableServiceEntity;
import ru.hits.attackdefenceplatform.modules.vulnerable_service.repository.VulnerableServiceRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Провайдер для работы с сущностью уязвимого сервиса.
 */
@Service
@RequiredArgsConstructor
public class VulnerableServiceProvider extends EntityProvider<VulnerableServiceEntity> {

    private final VulnerableServiceRepository vulnerableServiceRepository;

    @Override
    protected Optional<VulnerableServiceEntity> findById(UUID id) {
        return vulnerableServiceRepository.findById(id);
    }

    @Override
    protected String entityName() {
        return "Сервис";
    }

    @Override
    protected JpaRepository<VulnerableServiceEntity, UUID> getRepository() {
        return vulnerableServiceRepository;
    }
}

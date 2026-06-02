package ru.hits.attackdefenceplatform.core.flag;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.attackdefenceplatform.common.exception.TeamNotFoundException;
import ru.hits.attackdefenceplatform.core.flag.mapper.FlagMapper;
import ru.hits.attackdefenceplatform.core.flag.repository.FlagEntity;
import ru.hits.attackdefenceplatform.core.flag.repository.FlagRepository;
import ru.hits.attackdefenceplatform.core.team.repository.TeamRepository;
import ru.hits.attackdefenceplatform.core.vulnerable_service.repository.VulnerableServiceRepository;
import ru.hits.attackdefenceplatform.public_interface.flag.CreateFlagRequest;
import ru.hits.attackdefenceplatform.public_interface.flag.FlagDto;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Сервис для административного управления флагами.
 */
@Service
@RequiredArgsConstructor
public class FlagManagementServiceImpl implements FlagManagementService {
    private final FlagRepository flagRepository;
    private final TeamRepository teamRepository;
    private final VulnerableServiceRepository vulnerableServiceRepository;

    /**
     * Создает флаги для указанной команды и сервиса.
     *
     * @param request данные для создания флагов
     */
    @Override
    @Transactional
    public void createFlags(CreateFlagRequest request) {
        var team = teamRepository.findById(request.teamId())
                .orElseThrow(() -> new TeamNotFoundException("Команда с ID " + request.teamId() + " не найдена"));

        var service = vulnerableServiceRepository.findById(request.serviceId())
                .orElseThrow(() -> new EntityNotFoundException("Сервис с ID " + request.serviceId() + " не найден"));

        for (String value : request.values()) {
            var flag = FlagMapper.fromCreateFlagRequest(value, team, service);
            flagRepository.save(flag);
        }
    }

    /**
     * Возвращает список флагов с пагинацией и фильтрацией по имени команды, сервиса, значению и статусу.
     *
     * @param page Номер страницы
     * @param size Количество элементов на странице
     * @param search Строка поиска по имени команды, сервиса или значению флага
     * @param isActive Статус активности флага
     * @return объект Page с флагами
     */
    @Override
    @Transactional(readOnly = true)
    public Page<FlagDto> getAllFlags(int page, int size, String search, Boolean isActive) {
        Pageable pageable = PageRequest.of(page, size);

        Specification<FlagEntity> spec = createFlagSpecification(search, isActive);
        Page<FlagEntity> flagPage = flagRepository.findAll(spec, pageable);
        return flagPage.map(FlagMapper::mapToFlagDto);
    }

    /**
     * Возвращает флаг по его ID.
     *
     * @param id идентификатор флага
     * @return FlagDto
     */
    @Override
    @Transactional(readOnly = true)
    public FlagDto getFlagById(UUID id) {
        var flag = flagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Флаг с ID " + id + " не найден"));
        return FlagMapper.mapToFlagDto(flag);
    }

    /**
     * Удаляет флаг по ID.
     *
     * @param id идентификатор флага
     */
    @Override
    @Transactional
    public void deleteFlag(UUID id) {
        if (!flagRepository.existsById(id)) {
            throw new EntityNotFoundException("Флаг с ID " + id + " не найден");
        }
        flagRepository.deleteById(id);
    }

    /**
     * Переключает статус активности флага.
     *
     * @param id идентификатор флага
     * @return обновлённый FlagDto
     */
    @Override
    public FlagDto changeFlagStatus(UUID id) {
        var flag = flagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Флаг с ID " + id + " не найден"));
        flag.setIsActive(!flag.getIsActive());
        var newFlag = flagRepository.save(flag);
        return FlagMapper.mapToFlagDto(newFlag);
    }

    /**
     * Возвращает флаги для указанного сервиса.
     *
     * @param serviceId идентификатор сервиса
     * @return список FlagDto
     */
    @Override
    @Transactional(readOnly = true)
    public List<FlagDto> getFlagsByService(UUID serviceId) {
        var service = vulnerableServiceRepository.findById(serviceId)
                .orElseThrow(() -> new EntityNotFoundException("Сервис с ID " + serviceId + " не найден"));

        return flagRepository.findByVulnerableService(service).stream()
                .map(FlagMapper::mapToFlagDto)
                .toList();
    }

    /**
     * Возвращает флаги для указанной команды.
     *
     * @param teamId идентификатор команды
     * @return список FlagDto
     */
    @Override
    @Transactional(readOnly = true)
    public List<FlagDto> getFlagsByTeam(UUID teamId) {
        var team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Команда с ID " + teamId + " не найдена"));

        return flagRepository.findByFlagOwner(team).stream()
                .map(FlagMapper::mapToFlagDto)
                .toList();
    }

    /**
     * Деактивирует все флаги для заданной команды и сервиса.
     *
     * @param serviceId идентификатор сервиса
     * @param teamId идентификатор команды
     */
    @Transactional
    public void disableAllFlagsForTeam(UUID serviceId, UUID teamId) {
        var flags = flagRepository.findFlagsByServiceAndTeam(serviceId, teamId);
        flags.forEach(flag -> flag.setIsActive(false));
        flagRepository.saveAll(flags);
    }

    /**
     * Создает спецификацию для поиска и фильтрации флагов.
     *
     * @param search Строка поиска
     * @param isActive Статус активности флага
     * @return спецификация для поиска
     */
    private Specification<FlagEntity> createFlagSpecification(String search, Boolean isActive) {
        return (root, query, builder) -> {
            var predicate = builder.conjunction();

            if (search != null && !search.isBlank()) {
                var pattern = "%" + search.trim().toLowerCase(Locale.ROOT) + "%";
                predicate = builder.and(predicate, builder.or(
                        builder.like(builder.lower(root.get("flagOwner").get("name")), pattern),
                        builder.like(builder.lower(root.get("vulnerableService").get("name")), pattern),
                        builder.like(builder.lower(root.get("value")), pattern)
                ));
            }

            if (isActive != null) {
                predicate = builder.and(predicate, builder.equal(root.get("isActive"), isActive));
            }

            return predicate;
        };
    }
}


package ru.hits.attackdefenceplatform.core.flag;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.attackdefenceplatform.common.exception.TeamNotFoundException;
import ru.hits.attackdefenceplatform.core.flag.mapper.FlagMapper;
import ru.hits.attackdefenceplatform.core.flag.repository.FlagRepository;
import ru.hits.attackdefenceplatform.core.team.repository.TeamRepository;
import ru.hits.attackdefenceplatform.core.vulnerable.repository.VulnerableServiceRepository;
import ru.hits.attackdefenceplatform.public_interface.flag.CreateFlagRequest;
import ru.hits.attackdefenceplatform.public_interface.flag.FlagDto;
import ru.hits.attackdefenceplatform.public_interface.flag.FlagListDto;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminFlagServiceImpl implements AdminFlagService {
    private final FlagRepository flagRepository;
    private final TeamRepository teamRepository;
    private final VulnerableServiceRepository vulnerableServiceRepository;

    @Override
    @Transactional
    public FlagDto createFlag(CreateFlagRequest request) {
        var team = teamRepository.findById(request.teamId())
                .orElseThrow(() -> new TeamNotFoundException("Команда с ID " + request.teamId() + " не найдена"));

        var service = vulnerableServiceRepository.findById(request.serviceId())
                .orElseThrow(() -> new EntityNotFoundException("Сервис с ID " + request.serviceId() + " не найден"));

        if (flagRepository.existsByVulnerableServiceAndFlagNumber(service, request.flagNumberInService())) {
            throw new IllegalArgumentException("Флаг с таким номером уже существует в указанном сервисе");
        }

        var flag = FlagMapper.fromCreateFlagRequest(request, team, service);

        var savedFlag = flagRepository.save(flag);
        return FlagMapper.mapToFlagDto(savedFlag);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlagListDto> getAllFlags() {
        return flagRepository.findAll().stream()
                .map(FlagMapper::mapToFlagListDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public FlagDto getFlagById(UUID id) {
        var flag = flagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Флаг с ID " + id + " не найден"));
        return FlagMapper.mapToFlagDto(flag);
    }

    @Override
    @Transactional
    public void deleteFlag(UUID id) {
        if (!flagRepository.existsById(id)) {
            throw new EntityNotFoundException("Флаг с ID " + id + " не найден");
        }
        flagRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateFlag(UUID id, CreateFlagRequest request) {
        var flag = flagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Флаг с ID " + id + " не найден"));

        var team = teamRepository.findById(request.teamId())
                .orElseThrow(() -> new EntityNotFoundException("Команда с ID " + request.teamId() + " не найдена"));

        var service = vulnerableServiceRepository.findById(request.serviceId())
                .orElseThrow(() -> new EntityNotFoundException("Сервис с ID " + request.serviceId() + " не найден"));

        if (!flag.getVulnerableService().equals(service) &&
                flagRepository.existsByVulnerableServiceAndFlagNumber(service, request.flagNumberInService())) {
            throw new IllegalArgumentException("Флаг с таким номером уже существует в указанном сервисе");
        }

        flag.setPoint(request.points());
        flag.setFlagOwner(team);
        flag.setVulnerableService(service);
        flag.setFlagNumber(request.flagNumberInService());
        flagRepository.save(flag);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlagListDto> getFlagsByService(UUID serviceId) {
        var service = vulnerableServiceRepository.findById(serviceId)
                .orElseThrow(() -> new EntityNotFoundException("Сервис с ID " + serviceId + " не найден"));

        return flagRepository.findByVulnerableService(service).stream()
                .map(FlagMapper::mapToFlagListDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlagListDto> getFlagsByTeam(UUID teamId) {
        var team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Команда с ID " + teamId + " не найдена"));

        return flagRepository.findByFlagOwner(team).stream()
                .map(FlagMapper::mapToFlagListDto)
                .toList();
    }
}

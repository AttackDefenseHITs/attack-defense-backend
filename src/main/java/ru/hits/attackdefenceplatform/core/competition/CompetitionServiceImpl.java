package ru.hits.attackdefenceplatform.core.competition;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.attackdefenceplatform.common.DomainEventPublisher;
import ru.hits.attackdefenceplatform.common.exception.CompetitionException;
import ru.hits.attackdefenceplatform.configuration.properties.CompetitionDefaultsProperties;
import ru.hits.attackdefenceplatform.core.competition.mapper.CompetitionMapper;
import ru.hits.attackdefenceplatform.core.competition.repository.Competition;
import ru.hits.attackdefenceplatform.core.competition.enums.CompetitionAction;
import ru.hits.attackdefenceplatform.core.competition.repository.CompetitionRepository;
import ru.hits.attackdefenceplatform.core.competition.enums.CompetitionStatus;
import ru.hits.attackdefenceplatform.core.competition.state.CompetitionStateFactory;
import ru.hits.attackdefenceplatform.core.dashboard.repository.FlagSubmissionRepository;
import ru.hits.attackdefenceplatform.core.flag.repository.FlagRepository;
import ru.hits.attackdefenceplatform.core.service_status.repository.ServiceStatusRepository;
import ru.hits.attackdefenceplatform.core.team.repository.TeamMemberRepository;
import ru.hits.attackdefenceplatform.public_interface.competition.CompetitionShortDto;
import ru.hits.attackdefenceplatform.public_interface.competition.UpdateCompetitionModeRequest;
import ru.hits.attackdefenceplatform.public_interface.competition.CompetitionDto;
import ru.hits.attackdefenceplatform.public_interface.competition.UpdateCompetitionRequest;
import ru.hits.attackdefenceplatform.publisher.CompetitionEvent;
import ru.hits.attackdefenceplatform.publisher.CompetitionResetEvent;

import java.util.List;

/**
 * Сервис для управления процессом прохождения соревнования
 */
@Service
@RequiredArgsConstructor
public class CompetitionServiceImpl implements CompetitionService {

    private final CompetitionRepository competitionRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final FlagSubmissionRepository flagSubmissionRepository;
    private final ServiceStatusRepository serviceStatusRepository;
    private final FlagRepository flagRepository;

    private final CompetitionStateFactory stateFactory;
    private final CompetitionDefaultsProperties defaults;
    private final DomainEventPublisher eventPublisher;

    private final CompetitionInMemoryCache competitionCache;

    /**
     * Получить сущность соревнования (из кэша)
     */
    @Override
    @Transactional(readOnly = true)
    public Competition getCompetition() {
        return competitionCache.get();
    }

    /**
     * Изменение статуса соревнования
     */
    @Override
    @Transactional
    public CompetitionDto changeCompetitionStatus(CompetitionAction action) {
        var competition = getCompetition();

        var state = stateFactory.getState(competition.getStatus());
        state.handle(competition, action);

        var saved = competitionRepository.save(competition);
        competitionCache.update(saved);

        eventPublisher.publish(new CompetitionEvent(stateFactory.getMessage(action)));
        return CompetitionMapper.mapToCompetitionDto(saved);
    }

    /**
     * Возможные действия в зависимости от статуса
     */
    @Override
    @Transactional(readOnly = true)
    public List<CompetitionAction> getAvailableActions() {
        var competition = getCompetition();
        var state = stateFactory.getState(competition.getStatus());
        return state.getAvailableActions();
    }

    /**
     * Обновление настроек соревнования
     */
    @Override
    @Transactional
    public CompetitionDto updateCompetition(UpdateCompetitionRequest request) {
        var competition = getCompetition();

        competition.setName(request.name());
        competition.setStartDate(request.startDate());
        competition.setEndDate(request.endDate());
        competition.setTotalRounds(request.totalRounds());
        competition.setFlagSendCost(request.flagSendCost());
        competition.setFlagLostCost(request.flagLostCost());
        competition.setRoundDurationMinutes(request.roundDurationMinutes());
        competition.setRules(request.rules());

        var saved = competitionRepository.save(competition);
        competitionCache.update(saved);

        return CompetitionMapper.mapToCompetitionDto(saved);
    }

    /**
     * Изменение режима соревнования
     */
    @Override
    @Transactional
    public CompetitionDto updateCompetitionMode(UpdateCompetitionModeRequest request) {
        var competition = getCompetition();

        if (competition.getStatus() != CompetitionStatus.NEW) {
            throw new CompetitionException("Во время соревнования нельзя изменить режим");
        }

        competition.setCompetitionMode(request.competitionMode());

        var saved = competitionRepository.save(competition);
        competitionCache.update(saved);

        return CompetitionMapper.mapToCompetitionDto(saved);
    }

    /**
     * DTO текущего соревнования
     */
    @Override
    @Transactional(readOnly = true)
    public CompetitionDto getCompetitionDto() {
        return CompetitionMapper.mapToCompetitionDto(getCompetition());
    }

    /**
     * Краткое DTO текущего соревнования
     */
    @Override
    @Transactional(readOnly = true)
    public CompetitionShortDto getCompetitionShortDto() {
        Long flagCounts = flagSubmissionRepository.countByIsCorrectTrue();
        Long servicesCount = serviceStatusRepository.countDistinctServices();
        return CompetitionMapper.mapToCompetitionShortDto(getCompetition(), flagCounts, servicesCount);
    }

    /**
     * Перезапуск соревнования
     */
    @Override
    @Transactional
    public CompetitionDto restartCompetition() {
        var competition = getCompetition();

        competition.setStatus(CompetitionStatus.NEW);
        competition.setTotalRounds(defaults.getTotalRounds());
        competition.setRoundDurationMinutes(defaults.getRoundDurationMinutes());
        competition.setStartDate(null);
        competition.setEndDate(null);
        competition.setFlagSendCost(defaults.getFlagSendCost());
        competition.setFlagLostCost(defaults.getFlagLostCost());
        competition.setCurrentRound(0);

        serviceStatusRepository.deleteAll();
        flagRepository.deleteAll();
        flagSubmissionRepository.deleteAll();
        teamMemberRepository.deleteAll();

        eventPublisher.publish(new CompetitionResetEvent());

        var saved = competitionRepository.save(competition);
        competitionCache.update(saved);

        return CompetitionMapper.mapToCompetitionDto(saved);
    }

    /**
     * Начало следующего раунда
     */
    @Override
    @Transactional
    public CompetitionDto startNextRound() {
        var competition = getCompetition();

        if (competition.getStatus() != CompetitionStatus.IN_PROGRESS) {
            throw new CompetitionException("Нельзя начинать новый раунд, если соревнование не в состоянии IN_PROGRESS");
        }

        if (competition.getCurrentRound() >= competition.getTotalRounds()) {
            throw new CompetitionException("Все раунды уже завершены");
        }

        competition.setCurrentRound(competition.getCurrentRound() + 1);

        var saved = competitionRepository.save(competition);
        competitionCache.update(saved);

        return CompetitionMapper.mapToCompetitionDto(saved);
    }
}

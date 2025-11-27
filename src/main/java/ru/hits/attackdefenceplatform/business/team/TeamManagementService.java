package ru.hits.attackdefenceplatform.business.team;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.attackdefenceplatform.common.exception.TeamNotFoundException;
import ru.hits.attackdefenceplatform.business.team.repository.TeamEntity;
import ru.hits.attackdefenceplatform.business.team.repository.TeamRepository;
import ru.hits.attackdefenceplatform.public_interface.team.CreateManyTeamsRequest;
import ru.hits.attackdefenceplatform.public_interface.team.CreateTeamRequest;
import ru.hits.attackdefenceplatform.public_interface.team.CreatedTeamResponse;
import ru.hits.attackdefenceplatform.common.util.ColorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamManagementService {
    private final TeamRepository teamRepository;

    /**
     * Создает новую команду.
     *
     * @param request данные для создания команды
     * @return DTO новой команды
     */
    @Transactional
    public CreatedTeamResponse createTeam(CreateTeamRequest request) {
        var team = new TeamEntity();
        team.setName(request.name());
        team.setMaxMembers(request.maxMembers());
        team.setColor(ColorUtils.generateRandomColor());
        var newTeam = teamRepository.save(team);
        return new CreatedTeamResponse(
                newTeam.getId(),
                newTeam.getName(),
                0L,
                newTeam.getMaxMembers()
        );
    }

    /**
     * Обновляет данные команды.
     *
     * @param teamId идентификатор команды
     * @param request новые данные для команды
     */
    @Transactional
    public void updateTeam(UUID teamId, CreateTeamRequest request) {
        var team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Команда не найдена"));
        Optional.ofNullable(request.name()).filter(n -> !n.isBlank()).ifPresent(team::setName);
        Optional.ofNullable(request.maxMembers()).ifPresent(team::setMaxMembers);
        teamRepository.save(team);
    }

    /**
     * Удаляет команду по ID.
     *
     * @param id идентификатор команды
     */
    @Transactional
    public void deleteTeam(UUID id) {
        teamRepository.deleteById(id);
    }

    /**
     * Создает множество команд.
     *
     * @param request данные для создания команд
     * @return список DTO созданных команд
     */
    @Transactional
    public List<CreatedTeamResponse> createManyTeams(CreateManyTeamsRequest request) {
        var responses = new ArrayList<CreatedTeamResponse>();
        for (long i = 1; i <= request.teamsCount(); i++) {
            var teamName = "Команда " + i;
            responses.add(createTeam(new CreateTeamRequest(teamName, request.maxMembers())));
        }
        return responses;
    }
}


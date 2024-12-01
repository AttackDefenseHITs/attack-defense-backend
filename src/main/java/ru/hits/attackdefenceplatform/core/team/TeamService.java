package ru.hits.attackdefenceplatform.core.team;

import ru.hits.attackdefenceplatform.core.user.repository.UserEntity;
import ru.hits.attackdefenceplatform.public_interface.team.CreateManyTeamsRequest;
import ru.hits.attackdefenceplatform.public_interface.team.CreateTeamRequest;
import ru.hits.attackdefenceplatform.public_interface.team.TeamInfoDto;
import ru.hits.attackdefenceplatform.public_interface.team.TeamListDto;

import java.util.List;
import java.util.UUID;

public interface TeamService {
    UUID createTeam(CreateTeamRequest request);
    void deleteTeam(UUID id);
    void joinToTeam(UserEntity user, UUID teamId);
    void leftFromTeam(UserEntity user, UUID teamId);
    TeamInfoDto getTeamById(UUID teamId, UserEntity user);
    List<TeamListDto> getAllTeams();
    List<UUID> createManyTeams(CreateManyTeamsRequest request);
    void updateTeam(UUID teamId, CreateTeamRequest request);
}

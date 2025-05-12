package ru.hits.attackdefenceplatform.core.team;

import ru.hits.attackdefenceplatform.core.team.repository.TeamEntity;
import ru.hits.attackdefenceplatform.core.user.repository.UserEntity;
import ru.hits.attackdefenceplatform.public_interface.team.CreateManyTeamsRequest;
import ru.hits.attackdefenceplatform.public_interface.team.CreateTeamRequest;
import ru.hits.attackdefenceplatform.public_interface.team.CreatedTeamResponse;
import ru.hits.attackdefenceplatform.public_interface.team.TeamInfoDto;
import ru.hits.attackdefenceplatform.public_interface.team.TeamListDto;
import ru.hits.attackdefenceplatform.public_interface.team.TeamShortDataDto;
import ru.hits.attackdefenceplatform.public_interface.user.UserTeamMemberDto;

import java.util.List;
import java.util.UUID;

public interface TeamService {
    CreatedTeamResponse createTeam(CreateTeamRequest request);
    void deleteTeam(UUID id);
    void joinToTeam(UserEntity user, UUID teamId);
    void leftFromTeam(UserEntity user, UUID teamId);
    TeamInfoDto getTeamById(UUID teamId, UserEntity user);
    List<TeamListDto> getAllTeams(UserEntity user);
    List<CreatedTeamResponse> createManyTeams(CreateManyTeamsRequest request);
    void updateTeam(UUID teamId, CreateTeamRequest request);
    void removeMemberFromTeam(UUID teamId, UUID userId);
    TeamShortDataDto mapToTeamServiceStatusDto(TeamEntity team);
    List<UserTeamMemberDto> getTeamMemberRatings();
}

package ru.hits.attackdefenceplatform.business.team;

import ru.hits.attackdefenceplatform.business.team.repository.TeamEntity;
import ru.hits.attackdefenceplatform.business.user.repository.UserEntity;
import ru.hits.attackdefenceplatform.public_interface.team.TeamInfoDto;
import ru.hits.attackdefenceplatform.public_interface.team.TeamListDto;
import ru.hits.attackdefenceplatform.public_interface.team.TeamShortDataDto;
import ru.hits.attackdefenceplatform.public_interface.user.UserTeamMemberDto;

import java.util.List;
import java.util.UUID;

public interface TeamService {
    void joinToTeam(UserEntity user, UUID teamId);
    void leftFromTeam(UserEntity user, UUID teamId);
    TeamInfoDto getTeamById(UUID teamId, UserEntity user);
    List<TeamListDto> getAllTeams(UserEntity user);
    void removeMemberFromTeam(UUID teamId, UUID userId);
    TeamShortDataDto mapToTeamServiceStatusDto(TeamEntity team);
    List<UserTeamMemberDto> getTeamMemberRatings();
}

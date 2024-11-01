package ru.hits.attackdefenceplatform.core.team;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.attackdefenceplatform.core.team.repository.TeamMemberEntity;
import ru.hits.attackdefenceplatform.core.team.repository.TeamEntity;
import ru.hits.attackdefenceplatform.core.team.repository.TeamMemberRepository;
import ru.hits.attackdefenceplatform.core.team.repository.TeamRepository;
import ru.hits.attackdefenceplatform.core.user.repository.UserEntity;
import ru.hits.attackdefenceplatform.core.user.repository.UserRepository;
import ru.hits.attackdefenceplatform.public_interface.team.CreateTeamRequest;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Transactional
    public UUID createTeam(CreateTeamRequest request){
        var team = new TeamEntity();
        team.setName(request.name());
        team.setMaxMembers(request.maxMembers());
        var newTeam = teamRepository.save(team);

        return newTeam.getId();
    }

    @Transactional
    public void deleteTeam(UUID id){
        teamRepository.deleteById(id);
    }

    @Transactional
    public void joinToTeam(UserEntity user, UUID teamId) {
        var team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Команда с ID " + teamId + " не найдена"));

        long currentMembersCount = teamMemberRepository.countByTeam(team);
        if (currentMembersCount >= team.getMaxMembers()) {
            throw new RuntimeException("В команде с ID " + teamId + " нет места для нового участника");
        }

        var teamMember = new TeamMemberEntity();
        teamMember.setUser(user);
        teamMember.setTeam(team);

        teamMemberRepository.save(teamMember);
    }

    @Transactional
    public void leftFromTeam(UserEntity user, UUID teamId) {
        var team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Команда с ID " + teamId + " не найдена"));

        var teamMember = teamMemberRepository.findByUserAndTeam(user, team)
                .orElseThrow(() -> new RuntimeException("Пользователь не состоит в команде с ID " + teamId));

        teamMemberRepository.delete(teamMember);
    }
}

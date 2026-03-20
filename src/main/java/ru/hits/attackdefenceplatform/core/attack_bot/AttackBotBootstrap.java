package ru.hits.attackdefenceplatform.core.attack_bot;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.attackdefenceplatform.core.team.repository.TeamEntity;
import ru.hits.attackdefenceplatform.core.team.repository.TeamMemberEntity;
import ru.hits.attackdefenceplatform.core.team.repository.TeamMemberRepository;
import ru.hits.attackdefenceplatform.core.team.repository.TeamRepository;
import ru.hits.attackdefenceplatform.core.user.repository.Role;
import ru.hits.attackdefenceplatform.core.user.repository.UserEntity;
import ru.hits.attackdefenceplatform.core.user.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class AttackBotBootstrap {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        var botTeam = teamRepository.findByName(BotConstants.BOT_TEAM_NAME)
                .orElseGet(this::createBotTeam);

        var botUser = userRepository.findByLogin(BotConstants.BOT_USER_LOGIN)
                .orElseGet(this::createBotUser);

        Optional<TeamMemberEntity> existingMember = teamMemberRepository.findByUserAndTeam(botUser, botTeam);
        if (existingMember.isEmpty()) {
            var teamMember = new TeamMemberEntity();
            teamMember.setUser(botUser);
            teamMember.setTeam(botTeam);
            teamMember.setPoints(0.0);
            teamMemberRepository.save(teamMember);
        }
    }

    private TeamEntity createBotTeam() {
        var team = new TeamEntity();
        team.setName(BotConstants.BOT_TEAM_NAME);
        team.setMaxMembers(1L);
        team.setColor(BotConstants.BOT_TEAM_COLOR);
        team.setIsSystem(true);
        return teamRepository.save(team);
    }

    private UserEntity createBotUser() {
        var user = new UserEntity();
        user.setLogin(BotConstants.BOT_USER_LOGIN);
        user.setName(BotConstants.BOT_USER_NAME);
        user.setRole(Role.USER);
        user.setIsSystem(true);
        user.setPassword(bCryptPasswordEncoder.encode("__attack_bot_password__"));
        return userRepository.save(user);
    }
}

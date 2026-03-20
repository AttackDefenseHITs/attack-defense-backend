package ru.hits.attackdefenceplatform.core.attack_bot;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.team.repository.TeamEntity;
import ru.hits.attackdefenceplatform.core.team.repository.TeamRepository;
import ru.hits.attackdefenceplatform.core.user.repository.UserEntity;
import ru.hits.attackdefenceplatform.core.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AttackBotIdentityService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public TeamEntity getBotTeam() {
        return teamRepository.findByName(BotConstants.BOT_TEAM_NAME)
                .orElseThrow(() -> new IllegalStateException("Bot team not found"));
    }

    public UserEntity getBotUser() {
        return userRepository.findByLogin(BotConstants.BOT_USER_LOGIN)
                .orElseThrow(() -> new IllegalStateException("Bot user not found"));
    }
}

package ru.hits.attackdefenceplatform.core.flag;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.hits.attackdefenceplatform.core.attack_bot.AttackBotIdentityService;
import ru.hits.attackdefenceplatform.publisher.AttackBotCapturedFlagsEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class AttackBotFlagSubmissionListener {

    private final EventBus eventBus;
    private final FlagService flagService;
    private final AttackBotIdentityService attackBotIdentityService;

    @PostConstruct
    public void register() {
        eventBus.register(this);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onCapturedFlags(AttackBotCapturedFlagsEvent event) {
        var botUser = attackBotIdentityService.getBotUser();

        for (String flag : event.flags()) {
            try {
                flagService.sendFlag(flag, botUser);
                log.info("Attack bot submitted flag successfully: {}", flag);
            } catch (Exception e) {
                log.warn("Attack bot failed to submit flag '{}': {}", flag, e.getMessage());
            }
        }
    }
}

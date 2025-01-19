package ru.hits.attackdefenceplatform.core.checker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.checker.enums.CheckerResult;

import java.util.UUID;

@Service
@Slf4j
public class CheckerResultHandler {
    public void handleCheckerResult(UUID serviceId, UUID teamId, CheckerResult result) {
        switch (result) {
            case OK -> {
                log.info("Checker OK for service: {}, team: {}", serviceId, teamId);
                createFlags(serviceId, teamId);
                notifyClients(serviceId, teamId, "Checker passed");
            }
            case MUMBLE, CORRUPT, DOWN -> {
                log.warn("Checker issue detected for service: {}, team: {}, result: {}", serviceId, teamId, result);
                notifyClients(serviceId, teamId, "Checker reported an issue: " + result);
            }
            case CHECK_FAILED -> {
                log.error("Checker failed for service: {}, team: {}", serviceId, teamId);
                notifyClients(serviceId, teamId, "Checker execution failed");
            }
        }
    }

    private void createFlags(UUID serviceId, UUID teamId) {
        // Логика создания флагов (будет позже)
        log.info("Flags created for service: {}, team: {}", serviceId, teamId);
    }

    private void notifyClients(UUID serviceId, UUID teamId, String message) {
        // Логика отправки уведомлений (будет позже)
        log.info("Notification sent for service: {}, team: {}, message: {}", serviceId, teamId, message);
    }
}

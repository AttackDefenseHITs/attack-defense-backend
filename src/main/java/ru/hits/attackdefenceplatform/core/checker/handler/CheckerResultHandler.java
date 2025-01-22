package ru.hits.attackdefenceplatform.core.checker.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.checker.data.ScriptExecutionResult;
import ru.hits.attackdefenceplatform.core.flag.AdminFlagService;
import ru.hits.attackdefenceplatform.public_interface.flag.CreateFlagRequest;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckerResultHandler {
    private final AdminFlagService adminFlagService;

    public void handleCheckerResult(UUID serviceId, UUID teamId, ScriptExecutionResult result) {
        switch (result.getResult()) {
            case OK -> {
                log.info("Checker OK for service: {}, team: {}", serviceId, teamId);
            }
            case MUMBLE, CORRUPT, DOWN -> {
                log.warn("Checker issue detected for service: {}, team: {}, result: {}", serviceId, teamId, result);
            }
            case CHECK_FAILED -> {
                log.error("Checker failed for service: {}, team: {}", serviceId, teamId);
            }
        }

        var flags = result.getOutputLines();
        if (!flags.isEmpty()){
            createNewFlags(serviceId, teamId, flags);
        }
    }

    private void createNewFlags(UUID serviceId, UUID teamId, List<String> flags) {
        var request = new CreateFlagRequest(flags, serviceId, teamId);
        adminFlagService.createFlags(request);
        adminFlagService.disableAllFlagsForTeam(serviceId, teamId);
    }
}

package ru.hits.attackdefenceplatform.core.checker.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.checker.data.ScriptExecutionResult;
import ru.hits.attackdefenceplatform.core.flag.AdminFlagService;
import ru.hits.attackdefenceplatform.core.service_status.ServiceStatusService;
import ru.hits.attackdefenceplatform.public_interface.flag.CreateFlagRequest;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckerResultHandler {
    private final AdminFlagService adminFlagService;
    private final ServiceStatusService serviceStatusService;

    public void handleCheckerResult(UUID serviceId, UUID teamId, ScriptExecutionResult result) {
        //Логирование для дебага. Потом удалить
        switch (result.getCheckerResult()) {
            case OK -> log.info("Checker OK for service: {}, team: {}", serviceId, teamId);
            case MUMBLE, CORRUPT, DOWN ->
                    log.warn("Checker issue detected for service: {}, team: {}, result: {}", serviceId, teamId, result);
            case CHECK_FAILED -> log.error("Checker failed for service: {}, team: {}", serviceId, teamId);
        }

        serviceStatusService.updateServiceStatus(serviceId, teamId, result.getCheckerResult());

        var flags = result.getOutputLines();
        if (!flags.isEmpty()){
            createNewFlags(serviceId, teamId, flags);
        }
    }

    private void createNewFlags(UUID serviceId, UUID teamId, List<String> flags) {
        List<String> validFlags = flags.stream()
                .filter(flag -> flag.matches("^[A-Z0-9]{31}=$"))
                .toList();

        if (validFlags.isEmpty()) {
            log.warn("Нет валидных флагов для serviceId: {}, teamId: {}", serviceId, teamId);
            return;
        }

        adminFlagService.disableAllFlagsForTeam(serviceId, teamId);
        var request = new CreateFlagRequest(validFlags, serviceId, teamId);
        adminFlagService.createFlags(request);
    }
}

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

/**
 * Сервис для обработки результатов выполнения чекера.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CheckerResultHandler {
    private final AdminFlagService adminFlagService;
    private final ServiceStatusService serviceStatusService;

    private final static String FLAG_REGEX = "^[A-Z0-9]{31}=$";

    /**
     * Обрабатывает результат выполнения скрипта-чекера для заданного сервиса и команды.
     *
     * <p>В зависимости от результата проверки выполняется логирование, обновление статуса сервиса,
     * а также создание новых флагов, если они присутствуют в выводе скрипта.</p>
     *
     * @param serviceId идентификатор сервиса
     * @param teamId идентификатор команды
     * @param result объект, содержащий результаты выполнения скрипта-чекера и его вывод
     */
    public void handleCheckerResult(UUID serviceId, UUID teamId, ScriptExecutionResult result) {
        // Логирование для отладки. Позже удалить
        switch (result.getCheckerResult()) {
            case OK -> log.info("Проверка пройдена успешно для сервиса: {}, команды: {}", serviceId, teamId);
            case MUMBLE, CORRUPT, DOWN ->
                    log.warn("Обнаружена проблема при проверке для сервиса: {}, команды: {}, результат: {}", serviceId, teamId, result);
            case CHECK_FAILED -> log.error("Проверка не пройдена для сервиса: {}, команды: {}", serviceId, teamId);
        }

        serviceStatusService.updateServiceStatus(serviceId, teamId, result.getCheckerResult());

        var flags = result.getOutputLines();
        if (!flags.isEmpty()){
            createNewFlags(serviceId, teamId, flags);
        }
    }

    /**
     * Создает новые флаги для заданного сервиса и команды, если в выводе чекера присутствуют валидные флаги.
     *
     * <p>Метод фильтрует список строк, оставляя только те, которые соответствуют паттерну валидного флага,
     * затем отключает все существующие флаги для команды и создает новые на основе полученных данных.</p>
     *
     * @param serviceId идентификатор сервиса
     * @param teamId идентификатор команды
     * @param flags список строк, содержащих потенциальные флаги
     */
    private void createNewFlags(UUID serviceId, UUID teamId, List<String> flags) {
        List<String> validFlags = flags.stream()
                .filter(flag -> flag.matches(FLAG_REGEX))
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


package ru.hits.attackdefenceplatform.core.service_status;

import ru.hits.attackdefenceplatform.core.checker.enums.CheckerResult;
import ru.hits.attackdefenceplatform.core.service_status.repository.ServiceStatusEntity;
import ru.hits.attackdefenceplatform.public_interface.service_statuses.ServiceStatusDto;
import ru.hits.attackdefenceplatform.public_interface.service_statuses.ServiceStatusInfo;

import java.util.Optional;
import java.util.UUID;

public interface ServiceStatusService {
    /**
     * Обновляет статус сервиса для команды.
     *
     * @param serviceId ID сервиса.
     * @param teamId    ID команды.
     * @param result    Новый статус из CheckerResult.
     */
    void updateServiceStatus(UUID serviceId, UUID teamId, CheckerResult result);

    /**
     * Получает информацию о статусе сервиса для команды.
     *
     * @param serviceId ID сервиса.
     * @param teamId    ID команды.
     * @return Объект ServiceStatusEntity, если найден.
     */
    ServiceStatusDto getServiceStatus(UUID serviceId, UUID teamId);

    /**
     * Получает информацию о статусах всех сервисов
     */
    ServiceStatusInfo getAllServiceStatuses();

    /**
     * Создает новый статус для сервиса и команды, если он отсутствует.
     *
     * @param serviceId ID сервиса.
     * @param teamId    ID команды.
     * @return Объект ServiceStatusEntity.
     */
    ServiceStatusEntity createServiceStatus(UUID serviceId, UUID teamId);
}


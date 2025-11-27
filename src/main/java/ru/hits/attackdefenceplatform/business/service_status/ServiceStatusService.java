package ru.hits.attackdefenceplatform.business.service_status;

import ru.hits.attackdefenceplatform.public_interface.service_statuses.ServiceStatusInfo;

public interface ServiceStatusService {
    /**
     * Получает информацию о статусах всех сервисов
     */
    ServiceStatusInfo getAllServiceStatuses();
}


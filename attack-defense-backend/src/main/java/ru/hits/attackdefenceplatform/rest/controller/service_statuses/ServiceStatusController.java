package ru.hits.attackdefenceplatform.rest.controller.service_statuses;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hits.attackdefenceplatform.core.service_status.ServiceStatusService;
import ru.hits.attackdefenceplatform.public_interface.service_statuses.ServiceStatusInfo;

@RestController
@RequestMapping("api/statuses")
@Tag(name = "Информация о состоянии сервисов")
@RequiredArgsConstructor
public class ServiceStatusController {
    private final ServiceStatusService serviceStatusService;

    @GetMapping()
    @Operation(summary = "Получает информацию о состоянии всех сервисов команд")
    public ResponseEntity<ServiceStatusInfo> getServiceStatuses(){
        return ResponseEntity.ok(serviceStatusService.getAllServiceStatuses());
    }
}

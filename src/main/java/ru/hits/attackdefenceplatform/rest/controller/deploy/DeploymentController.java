package ru.hits.attackdefenceplatform.rest.controller.deploy;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.hits.attackdefenceplatform.core.deploy.deployment.DeploymentService;
import ru.hits.attackdefenceplatform.core.deploy.status.DeploymentStatusService;
import ru.hits.attackdefenceplatform.public_interface.deployment.DeployPossibility;
import ru.hits.attackdefenceplatform.public_interface.deployment.DeploymentResult;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/deploy")
@RequiredArgsConstructor
@Tag(name = "Управление деплоем уязвимых сервисов")
public class DeploymentController {

    private final DeploymentService deploymentService;
    private final DeploymentStatusService deploymentStatusService;

    @PostMapping("/all")
    @Operation(summary = "Деплой всех сервисов на все виртуальные машины")
    public ResponseEntity<String> deployAllServices() {
        deploymentService.deployAllServices();
        return ResponseEntity.ok("Начало деплоя");
    }

    @PostMapping("")
    @Operation(summary = "Деплой конкретного сервиса на указанной виртуальной машине")
    public ResponseEntity<String> deployServiceOnVirtualMachine(
            @RequestParam UUID serviceId,
            @RequestParam UUID virtualMachineId
    ) {
        deploymentService.deployServiceOnVirtualMachine(serviceId, virtualMachineId);
        return ResponseEntity.ok(
                String.format("Начало деплоя")
        );
    }

    @GetMapping("/status")
    @Operation(summary = "Получить таблицу результатов деплоя")
    public ResponseEntity<DeploymentResult> getAllDeploymentResults(){
        var results = deploymentStatusService.getAllDeploymentResults();
        return ResponseEntity.ok(results);
    }

    @GetMapping("/check")
    @Operation(summary = "Определяет доступность деплоя")
    public ResponseEntity<DeployPossibility> getDeployPossibility(){
        var result = new DeployPossibility(!deploymentService.isDeploymentInProgress());
        return ResponseEntity.ok(result);
    }
}

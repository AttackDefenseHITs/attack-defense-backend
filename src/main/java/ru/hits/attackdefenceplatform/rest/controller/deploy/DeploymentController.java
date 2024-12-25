package ru.hits.attackdefenceplatform.rest.controller.deploy;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.hits.attackdefenceplatform.core.deploy.DeploymentService;

@RestController
@RequestMapping("/api/admin/deploy")
@RequiredArgsConstructor
@Tag(name = "Управление деплоем уязвимых сервисов")
public class DeploymentController {

    private final DeploymentService deploymentService;

    @PostMapping("/all")
    @Operation(summary = "Деплой всех сервисов на все виртуальные машины")
    public ResponseEntity<String> deployAllServices() {
        deploymentService.deployAllServices();
        return ResponseEntity.ok("Все сервисы успешно задеплоены на все виртуальные машины.");
    }

    @PostMapping("")
    @Operation(summary = "Деплой конкретного сервиса на указанной виртуальной машине")
    public ResponseEntity<String> deployServiceOnVirtualMachine(
            @RequestParam String serviceName,
            @RequestParam String virtualMachineIp
    ) {
        deploymentService.deployServiceOnVirtualMachine(serviceName, virtualMachineIp);
        return ResponseEntity.ok(
                String.format("Сервис '%s' успешно задеплоен на виртуальной машине '%s'.", serviceName, virtualMachineIp)
        );
    }
}

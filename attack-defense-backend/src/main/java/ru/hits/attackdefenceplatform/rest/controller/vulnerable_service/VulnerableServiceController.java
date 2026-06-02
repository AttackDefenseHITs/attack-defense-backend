package ru.hits.attackdefenceplatform.rest.controller.vulnerable_service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hits.attackdefenceplatform.core.vulnerable_service.VulnerableService;
import ru.hits.attackdefenceplatform.public_interface.vulnerable_service.CreateVulnerableServiceRequest;
import ru.hits.attackdefenceplatform.public_interface.vulnerable_service.UpdateVulnerableServiceRequest;
import ru.hits.attackdefenceplatform.public_interface.vulnerable_service.VulnerableServiceDto;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/services")
@RequiredArgsConstructor
@Tag(name = "Управление уязвимыми сервисами")
public class VulnerableServiceController {

    private final VulnerableService vulnerableService;

    @PostMapping
    @Operation(summary = "Создать уязвимый сервис")
    public ResponseEntity<VulnerableServiceDto> createService(@RequestBody CreateVulnerableServiceRequest request) {
        var service = vulnerableService.createService(request);
        return ResponseEntity.ok(service);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить сервис по ID")
    public ResponseEntity<VulnerableServiceDto> getServiceById(@PathVariable UUID id) {
        var service = vulnerableService.getServiceById(id);
        return ResponseEntity.ok(service);
    }

    @GetMapping
    @Operation(summary = "Получить все сервисы")
    public ResponseEntity<List<VulnerableServiceDto>> getAllServices() {
        var services = vulnerableService.getAllServices();
        return ResponseEntity.ok(services);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить уязвимый сервис")
    public ResponseEntity<Void> updateService(
            @PathVariable UUID id,
            @RequestBody UpdateVulnerableServiceRequest request
    ) {
        vulnerableService.updateService(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить уязвимый сервис")
    public ResponseEntity<Void> deleteService(@PathVariable UUID id) {
        vulnerableService.deleteService(id);
        return ResponseEntity.ok().build();
    }
}


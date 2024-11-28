package ru.hits.attackdefenceplatform.rest.controller.virtual_machine;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
import ru.hits.attackdefenceplatform.core.virtual_machine.VirtualMachineService;
import ru.hits.attackdefenceplatform.public_interface.vitrual_machine.CreateVirtualMachineRequest;
import ru.hits.attackdefenceplatform.public_interface.vitrual_machine.UpdateVirtualMachineRequest;
import ru.hits.attackdefenceplatform.public_interface.vitrual_machine.VirtualMachineDto;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/admin/vms")
@Tag(name = "Виртуальные машины")
@RequiredArgsConstructor
public class VirtualMachineController {

    private final VirtualMachineService virtualMachineService;

    @PostMapping
    @Operation(summary = "Создать виртуальную машину")
    public ResponseEntity<UUID> createVirtualMachine(@RequestBody @Valid CreateVirtualMachineRequest request) {
        var vmId = virtualMachineService.createVirtualMachine(request);
        return ResponseEntity.ok(vmId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить информацию о виртуальной машине")
    public ResponseEntity<VirtualMachineDto> getVirtualMachineById(@PathVariable UUID id) {
        var vm = virtualMachineService.getVirtualMachineById(id);
        return ResponseEntity.ok(vm);
    }

    @GetMapping
    @Operation(summary = "Получить список всех виртуальных машин")
    public ResponseEntity<List<VirtualMachineDto>> getAllVirtualMachines() {
        var vms = virtualMachineService.getAllVirtualMachines();
        return ResponseEntity.ok(vms);
    }

    @GetMapping("/team/{teamId}")
    @Operation(summary = "Получить виртуальные машины команды")
    public ResponseEntity<List<VirtualMachineDto>> getVirtualMachinesByTeam(@PathVariable UUID teamId) {
        var vms = virtualMachineService.getVirtualMachinesByTeam(teamId);
        return ResponseEntity.ok(vms);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить информацию о виртуальной машине")
    public ResponseEntity<Void> updateVirtualMachine(@PathVariable UUID id,
                                                     @RequestBody @Valid UpdateVirtualMachineRequest request) {
        virtualMachineService.updateVirtualMachine(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить виртуальную машину")
    public ResponseEntity<Void> deleteVirtualMachine(@PathVariable UUID id) {
        virtualMachineService.deleteVirtualMachine(id);
        return ResponseEntity.ok().build();
    }
}


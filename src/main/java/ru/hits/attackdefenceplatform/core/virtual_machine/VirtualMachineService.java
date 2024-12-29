package ru.hits.attackdefenceplatform.core.virtual_machine;

import ru.hits.attackdefenceplatform.public_interface.vitrual_machine.CreateVirtualMachineRequest;
import ru.hits.attackdefenceplatform.public_interface.vitrual_machine.UpdateVirtualMachineRequest;
import ru.hits.attackdefenceplatform.public_interface.vitrual_machine.VirtualMachineDto;

import java.util.List;
import java.util.UUID;

public interface VirtualMachineService {
    VirtualMachineDto createVirtualMachine(CreateVirtualMachineRequest request);
    VirtualMachineDto getVirtualMachineById(UUID id);
    List<VirtualMachineDto> getAllVirtualMachines();
    List<VirtualMachineDto> getVirtualMachinesByTeam(UUID teamId);
    void updateVirtualMachine(UUID id, UpdateVirtualMachineRequest request);
    void deleteVirtualMachine(UUID id);
}


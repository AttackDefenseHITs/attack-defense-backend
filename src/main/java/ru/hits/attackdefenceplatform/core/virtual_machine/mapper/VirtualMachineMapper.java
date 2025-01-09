package ru.hits.attackdefenceplatform.core.virtual_machine.mapper;

import ru.hits.attackdefenceplatform.core.virtual_machine.repository.VirtualMachineEntity;
import ru.hits.attackdefenceplatform.public_interface.vitrual_machine.VirtualMachineDto;

public class VirtualMachineMapper {
    private VirtualMachineMapper() {}

    public static VirtualMachineDto toDto(VirtualMachineEntity vm) {
        return new VirtualMachineDto(
                vm.getId(),
                vm.getIpAddress(),
                vm.getUsername(),
                vm.getPassword(),
                vm.getTeam().getId(),
                vm.getTeam().getName()
        );
    }
}


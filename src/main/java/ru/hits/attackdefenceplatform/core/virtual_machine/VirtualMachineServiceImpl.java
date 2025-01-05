package ru.hits.attackdefenceplatform.core.virtual_machine;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.attackdefenceplatform.common.exception.TeamNotFoundException;
import ru.hits.attackdefenceplatform.core.deploy.status.mapper.DeploymentStatusInitializer;
import ru.hits.attackdefenceplatform.core.team.repository.TeamRepository;
import ru.hits.attackdefenceplatform.core.virtual_machine.mapper.VirtualMachineMapper;
import ru.hits.attackdefenceplatform.core.virtual_machine.repository.VirtualMachineEntity;
import ru.hits.attackdefenceplatform.core.virtual_machine.repository.VirtualMachineRepository;
import ru.hits.attackdefenceplatform.public_interface.vitrual_machine.CreateVirtualMachineRequest;
import ru.hits.attackdefenceplatform.public_interface.vitrual_machine.UpdateVirtualMachineRequest;
import ru.hits.attackdefenceplatform.public_interface.vitrual_machine.VirtualMachineDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VirtualMachineServiceImpl implements VirtualMachineService {

    private final VirtualMachineRepository virtualMachineRepository;
    private final DeploymentStatusInitializer deploymentStatusInitializer;
    private final TeamRepository teamRepository;

    @Override
    @Transactional
    public VirtualMachineDto createVirtualMachine(CreateVirtualMachineRequest request) {
        var team = teamRepository.findById(request.teamId())
                .orElseThrow(() -> new TeamNotFoundException("Команда с ID " + request.teamId() + " не найдена"));

        var vm = new VirtualMachineEntity();
        vm.setIpAddress(request.ipAddress());
        vm.setUsername(request.username());
        vm.setPassword(request.password());
        vm.setTeam(team);

        var savedVm = virtualMachineRepository.save(vm);
        deploymentStatusInitializer.initializeStatusesForNewVirtualMachine(savedVm.getId());

        return VirtualMachineMapper.toDto(savedVm);
    }

    @Override
    @Transactional(readOnly = true)
    public VirtualMachineDto getVirtualMachineById(UUID id) {
        var vm = virtualMachineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Виртуальная машина с ID " + id + " не найдена"));
        return VirtualMachineMapper.toDto(vm);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VirtualMachineDto> getAllVirtualMachines() {
        return virtualMachineRepository.findAll().stream()
                .map(VirtualMachineMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VirtualMachineDto> getVirtualMachinesByTeam(UUID teamId) {
        var team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Команда с ID " + teamId + " не найдена"));

        return virtualMachineRepository.findAllByTeam(team).stream()
                .map(VirtualMachineMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void updateVirtualMachine(UUID id, UpdateVirtualMachineRequest request) {
        var vm = virtualMachineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Виртуальная машина с ID " + id + " не найдена"));

        Optional.ofNullable(request.ipAddress()).ifPresent(vm::setIpAddress);
        Optional.ofNullable(request.username()).ifPresent(vm::setUsername);
        Optional.ofNullable(request.password()).ifPresent(vm::setPassword);

        if (request.teamId() != null) {
            var team = teamRepository.findById(request.teamId()).orElseThrow(() ->
                    new EntityNotFoundException("Команда с ID " + request.teamId() + " не найдена"));
            vm.setTeam(team);
        }

        virtualMachineRepository.save(vm);
    }


    @Override
    @Transactional
    public void deleteVirtualMachine(UUID id) {
        if (!virtualMachineRepository.existsById(id)) {
            throw new EntityNotFoundException("Виртуальная машина с ID " + id + " не найдена");
        }

        deploymentStatusInitializer.deleteStatusesForVirtualMachine(id);
        virtualMachineRepository.deleteById(id);
    }
}



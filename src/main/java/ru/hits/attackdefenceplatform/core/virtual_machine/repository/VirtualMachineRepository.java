package ru.hits.attackdefenceplatform.core.virtual_machine.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VirtualMachineRepository extends JpaRepository<VirtualMachineEntity, UUID> {
}

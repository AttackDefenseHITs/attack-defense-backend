package ru.hits.attackdefenceplatform.core.deploy.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import ru.hits.attackdefenceplatform.core.deploy.enums.DeploymentStatus;
import ru.hits.attackdefenceplatform.core.virtual_machine.repository.VirtualMachineEntity;
import ru.hits.attackdefenceplatform.core.vulnerable_service.repository.VulnerableServiceEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "deployment_statuses")
public class DeploymentStatusEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "virtual_machine_id", nullable = false)
    private VirtualMachineEntity virtualMachine;

    @ManyToOne
    @JoinColumn(name = "vulnerable_service_id", nullable = false)
    private VulnerableServiceEntity vulnerableService;

    @Enumerated(EnumType.STRING)
    private DeploymentStatus deploymentStatus;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}


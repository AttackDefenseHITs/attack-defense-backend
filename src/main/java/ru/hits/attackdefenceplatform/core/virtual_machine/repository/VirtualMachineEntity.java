package ru.hits.attackdefenceplatform.core.virtual_machine.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.hits.attackdefenceplatform.core.team.repository.TeamEntity;

import java.util.UUID;

@Entity
@Table(name = "virtual_machines")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VirtualMachineEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String ipAddress;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private TeamEntity team;
}

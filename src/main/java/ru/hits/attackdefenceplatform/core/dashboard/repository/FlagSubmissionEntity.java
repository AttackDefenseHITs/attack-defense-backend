package ru.hits.attackdefenceplatform.core.dashboard.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import ru.hits.attackdefenceplatform.core.flag.repository.FlagEntity;
import ru.hits.attackdefenceplatform.core.team.repository.TeamMemberEntity;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "flag_submissions")
@Data
public class FlagSubmissionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_member_id", nullable = false)
    private TeamMemberEntity teamMember;

    @Column(name = "submitted_flag", nullable = false)
    private String submittedFlag;

    @Column(name = "submission_time", nullable = false)
    private Date submissionTime;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flag_id")
    private FlagEntity flag; // Связь с правильным флагом (заполняется, если флаг корректен)
}


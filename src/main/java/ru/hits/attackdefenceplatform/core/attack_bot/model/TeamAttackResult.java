package ru.hits.attackdefenceplatform.core.attack_bot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamAttackResult {
    private UUID teamId;
    private double priority;   // итоговый приоритет
    private boolean eligible;  // можно ли атаковать команду в принципе
}

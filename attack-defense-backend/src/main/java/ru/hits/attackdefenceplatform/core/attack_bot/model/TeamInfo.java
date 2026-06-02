package ru.hits.attackdefenceplatform.core.attack_bot.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class TeamInfo {
    private UUID id;
    private String name;
}

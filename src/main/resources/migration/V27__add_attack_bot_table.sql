CREATE TABLE attack_bot_settings (
        id UUID PRIMARY KEY,
        enabled BOOLEAN NOT NULL,
        attack_probability DOUBLE PRECISION NOT NULL,
        max_targets INT NOT NULL,
        cooldown_rounds INT NOT NULL,
        priority_score_weight DOUBLE PRECISION NOT NULL,
        priority_sla_weight DOUBLE PRECISION NOT NULL,
        round_interval_seconds INT NOT NULL
);

INSERT INTO attack_bot_settings (
    id,
    enabled,
    attack_probability,
    max_targets,
    cooldown_rounds,
    priority_score_weight,
    priority_sla_weight,
    round_interval_seconds
) VALUES (
             '00000000-0000-0000-0000-000000000001',
             FALSE,
             0.5,
             3,
             1,
             1.0,
             1.0,
             30
);
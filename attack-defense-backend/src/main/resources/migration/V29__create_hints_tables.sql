CREATE TABLE IF NOT EXISTS service_hint_templates (
    id           UUID PRIMARY KEY,
    service_id   UUID NOT NULL,
    hint_level   INT  NOT NULL,
    hint_text    VARCHAR NOT NULL,
    multiplier   DOUBLE PRECISION NOT NULL,
    enabled      BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_service_hint_templates_service
    FOREIGN KEY (service_id)
    REFERENCES services(id)
    ON DELETE CASCADE,

    CONSTRAINT uq_service_hint_templates_service_level
    UNIQUE (service_id, hint_level),

    CONSTRAINT chk_service_hint_templates_multiplier
    CHECK (multiplier > 0 AND multiplier <= 1)
    );

CREATE INDEX IF NOT EXISTS idx_service_hint_templates_service_id
    ON service_hint_templates(service_id);

CREATE INDEX IF NOT EXISTS idx_service_hint_templates_service_level
    ON service_hint_templates(service_id, hint_level);


CREATE TABLE IF NOT EXISTS service_hint_purchases (
    id            UUID PRIMARY KEY,
    team_id       UUID NOT NULL,
    template_id   UUID NOT NULL,
    purchased_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    CONSTRAINT fk_service_hint_purchases_team
    FOREIGN KEY (team_id)
    REFERENCES teams(id) ON DELETE CASCADE,

    CONSTRAINT fk_service_hint_purchases_template
    FOREIGN KEY (template_id)
    REFERENCES service_hint_templates(id) ON DELETE CASCADE,

    CONSTRAINT uq_service_hint_purchases_team_template
    UNIQUE (team_id, template_id)
    );

CREATE INDEX IF NOT EXISTS idx_service_hint_purchases_team_id
    ON service_hint_purchases(team_id);

CREATE INDEX IF NOT EXISTS idx_service_hint_purchases_template_id
    ON service_hint_purchases(template_id);

CREATE INDEX IF NOT EXISTS idx_service_hint_purchases_team_template
    ON service_hint_purchases(team_id, template_id);
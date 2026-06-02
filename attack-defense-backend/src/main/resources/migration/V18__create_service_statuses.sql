CREATE TABLE service_statuses (
         id UUID PRIMARY KEY,
         service_id UUID NOT NULL,
         team_id UUID NOT NULL,
         last_status VARCHAR(50) NOT NULL,
         last_changed TIMESTAMP NOT NULL,
         total_ok_duration BIGINT NOT NULL DEFAULT 0,
         total_mumble_duration BIGINT NOT NULL DEFAULT 0,
         total_corrupt_duration BIGINT NOT NULL DEFAULT 0,
         total_down_duration BIGINT NOT NULL DEFAULT 0,
         created_at TIMESTAMP NOT NULL,
         updated_at TIMESTAMP NOT NULL,
         CONSTRAINT fk_service FOREIGN KEY (service_id) REFERENCES services (id) ON DELETE CASCADE,
         CONSTRAINT fk_team FOREIGN KEY (team_id) REFERENCES teams (id) ON DELETE CASCADE
);

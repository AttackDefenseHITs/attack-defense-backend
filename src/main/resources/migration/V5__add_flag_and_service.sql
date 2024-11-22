CREATE TABLE services (
       id UUID PRIMARY KEY,
       name VARCHAR(255) NOT NULL
);

CREATE TABLE flags (
       id UUID PRIMARY KEY,
       point INT NOT NULL,
       team_id UUID NOT NULL,
       vulnerable_service_id UUID NOT NULL,
       flag_number INT NOT NULL,
       is_active BOOLEAN NOT NULL DEFAULT TRUE,

       CONSTRAINT fk_flags_team FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE CASCADE,
       CONSTRAINT fk_flags_vulnerable_service FOREIGN KEY (vulnerable_service_id) REFERENCES services(id) ON DELETE CASCADE
);
CREATE TABLE virtual_machines (
    id UUID PRIMARY KEY,
    ip_address VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    team_id UUID NOT NULL,
    CONSTRAINT fk_team FOREIGN KEY (team_id) REFERENCES teams (id) ON DELETE CASCADE
);
CREATE TABLE teams (
       id UUID PRIMARY KEY,
       name VARCHAR(255) NOT NULL,
       max_members BIGINT NOT NULL
);

CREATE TABLE team_members (
       id UUID PRIMARY KEY,
       user_id UUID NOT NULL,
       team_id UUID NOT NULL,
       FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
       FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE CASCADE
);

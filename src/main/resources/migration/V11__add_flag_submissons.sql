CREATE TABLE flag_submissions (
     id UUID PRIMARY KEY,
     team_member_id UUID NOT NULL,
     submitted_flag VARCHAR NOT NULL,
     submission_time TIMESTAMP NOT NULL,
     is_correct BOOLEAN NOT NULL DEFAULT FALSE,
     flag_id UUID,

     CONSTRAINT fk_flag_submission_team_member FOREIGN KEY (team_member_id) REFERENCES team_members (id) ON DELETE SET NULL,
     CONSTRAINT fk_flag_submission_flag FOREIGN KEY (flag_id) REFERENCES flags (id) ON DELETE SET NULL
);
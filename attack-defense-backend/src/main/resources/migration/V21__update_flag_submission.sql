ALTER TABLE flag_submissions ADD COLUMN team_id UUID;
ALTER TABLE flag_submissions ADD COLUMN user_id UUID;

UPDATE flag_submissions fs
SET team_id = tm.team_id,
    user_id = tm.user_id
    FROM team_members tm
WHERE fs.team_member_id = tm.id;

ALTER TABLE flag_submissions
    ADD CONSTRAINT fk_flag_submission_team FOREIGN KEY (team_id) REFERENCES teams (id) ON DELETE CASCADE;

ALTER TABLE flag_submissions
    ADD CONSTRAINT fk_flag_submission_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;

ALTER TABLE flag_submissions DROP COLUMN team_member_id;

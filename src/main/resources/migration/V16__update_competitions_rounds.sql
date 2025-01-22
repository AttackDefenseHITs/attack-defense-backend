ALTER TABLE competitions ADD COLUMN total_rounds INT;
ALTER TABLE competitions RENAME COLUMN duration_minutes TO round_duration_minutes;


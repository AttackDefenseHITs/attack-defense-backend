ALTER TABLE services
    ADD COLUMN port INT;

UPDATE services
SET port = 8080
WHERE port IS NULL;

ALTER TABLE services
    ALTER COLUMN port SET NOT NULL;

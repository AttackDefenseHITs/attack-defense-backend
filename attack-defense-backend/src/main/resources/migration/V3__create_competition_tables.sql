CREATE TABLE competitions (
        id BIGINT NOT NULL PRIMARY KEY,
        status VARCHAR(255) NOT NULL,
        name VARCHAR(255) NOT NULL,
        start_date TIMESTAMP,
        end_date TIMESTAMP,
        duration_minutes INT,
        rules TEXT
);

INSERT INTO competitions (id, status, name, start_date, end_date, duration_minutes, rules)
VALUES
    (1, 'NEW', 'Attack-Defense', NULL, NULL, 180, NULL);
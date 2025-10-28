CREATE TABLE platform_repository (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL UNIQUE,
    url VARCHAR(512) NOT NULL,
    branch VARCHAR(100) NOT NULL DEFAULT 'main',
    type VARCHAR(50) NOT NULL,
    last_commit_sha VARCHAR(100),
    is_private BOOLEAN DEFAULT FALSE,
    last_synced_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
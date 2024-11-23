CREATE TABLE refresh_tokens (
       id UUID PRIMARY KEY,
       token VARCHAR(255) NOT NULL UNIQUE,
       user_id UUID NOT NULL,
       expiration_date TIMESTAMP NOT NULL,
       CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

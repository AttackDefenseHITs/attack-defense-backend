CREATE TABLE checkers (
       id UUID PRIMARY KEY,
       vulnerable_service_id UUID NOT NULL,
       script_file_path VARCHAR(255) NOT NULL,

       CONSTRAINT fk_checkers_vulnerable_service FOREIGN KEY (vulnerable_service_id) REFERENCES services(id) ON DELETE CASCADE
);
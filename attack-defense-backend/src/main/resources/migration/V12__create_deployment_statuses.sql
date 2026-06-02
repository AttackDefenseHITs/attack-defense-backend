CREATE TABLE deployment_statuses (
        id UUID PRIMARY KEY,
        virtual_machine_id UUID NOT NULL,
        vulnerable_service_id UUID NOT NULL,
        deployment_status VARCHAR(255) NOT NULL,
        message TEXT,
        updated_at TIMESTAMP NOT NULL,

        CONSTRAINT fk_virtual_machine
            FOREIGN KEY (virtual_machine_id) REFERENCES virtual_machines(id) ON DELETE CASCADE,

        CONSTRAINT fk_service
            FOREIGN KEY (vulnerable_service_id) REFERENCES services(id) ON DELETE CASCADE
);

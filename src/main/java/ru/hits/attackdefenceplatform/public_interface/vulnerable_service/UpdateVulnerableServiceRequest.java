package ru.hits.attackdefenceplatform.public_interface.vulnerable_service;

public record UpdateVulnerableServiceRequest(
        String name,
        String gitRepositoryUrl
) {}
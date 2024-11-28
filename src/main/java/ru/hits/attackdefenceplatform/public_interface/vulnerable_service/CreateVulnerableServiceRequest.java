package ru.hits.attackdefenceplatform.public_interface.vulnerable_service;

public record CreateVulnerableServiceRequest(
        String name,
        String gitRepositoryUrl
) {}

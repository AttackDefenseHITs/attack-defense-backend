package ru.hits.attackdefenceplatform.public_interface.hint;

import java.util.UUID;

public record BuyHintRequest(
        UUID templateId
) {}

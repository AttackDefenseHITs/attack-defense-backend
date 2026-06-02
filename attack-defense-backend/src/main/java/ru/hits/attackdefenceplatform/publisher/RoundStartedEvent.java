package ru.hits.attackdefenceplatform.publisher;

import java.time.LocalDateTime;

/**
 * Событие, публикуемое при начале нового раунда.
 */
public record RoundStartedEvent(int roundNumber, LocalDateTime startedAt) { }

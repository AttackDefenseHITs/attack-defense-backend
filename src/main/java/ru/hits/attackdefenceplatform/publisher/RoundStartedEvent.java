package ru.hits.attackdefenceplatform.publisher;

/**
 * Событие, публикуемое при начале нового раунда.
 */
public record RoundStartedEvent(int roundNumber) { }

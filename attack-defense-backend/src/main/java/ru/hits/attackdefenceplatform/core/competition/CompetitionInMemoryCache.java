package ru.hits.attackdefenceplatform.core.competition;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.hits.attackdefenceplatform.common.exception.CompetitionException;
import ru.hits.attackdefenceplatform.core.competition.repository.Competition;
import ru.hits.attackdefenceplatform.core.competition.repository.CompetitionRepository;

import java.util.concurrent.atomic.AtomicReference;

@Component
@RequiredArgsConstructor
@Slf4j
public class CompetitionInMemoryCache {

    private final CompetitionRepository competitionRepository;

    private final AtomicReference<Competition> cache = new AtomicReference<>();

    /**
     * Инициализация кэша при старте приложения
     */
    @EventListener(ApplicationStartedEvent.class)
    public void preloadCache() {
        try {
            competitionRepository.findAll().stream()
                    .findFirst().ifPresent(cache::set);

        } catch (Exception ex) {
            log.error("Ошибка заполнения кэша", ex);
        }
    }

    /**
     * Получение Competition (с InMemory cache)
     */
    public Competition get() {
        var cached = cache.get();
        if (cached != null) {
            return cached;
        }

        var competition = competitionRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new CompetitionException("Соревнование не найдено"));

        cache.set(competition);
        return competition;
    }

    /**
     * Обновление сущности в кэше
     */
    public void update(Competition competition) {
        cache.set(competition);
    }

    /**
     * Полная очистка кэша
     */
    public void invalidate() {
        cache.set(null);
    }
}

package ru.hits.attackdefenceplatform.business.token.job;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.hits.attackdefenceplatform.business.token.repository.RefreshTokenRepository;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class RefreshTokenCleanupScheduler {

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Удаляет токены, срок действия которых истёк.
     * Запускается каждый день в полночь.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void cleanExpiredTokens() {
        Date now = new Date();
        refreshTokenRepository.deleteByExpirationDateBefore(now);
    }
}

package ru.hits.attackdefenceplatform.core.token.job;

import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;
import ru.hits.attackdefenceplatform.core.token.repository.RefreshTokenRepository;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class RefreshTokenCleanupJob implements Job {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        refreshTokenRepository.deleteByExpirationDateBefore(new Date());
    }
}

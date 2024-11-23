package ru.hits.attackdefenceplatform.core.token.configuration;

import org.quartz.DateBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import ru.hits.attackdefenceplatform.core.token.job.RefreshTokenCleanupJob;

public class QuartzTokenConfiguration {
    @Bean
    public JobDetail refreshTokenCleanupJobDetail() {
        return JobBuilder.newJob(RefreshTokenCleanupJob.class)
                .withIdentity("refreshTokenCleanupJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger refreshTokenCleanupTrigger() {
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder
                .simpleSchedule()
                .withIntervalInHours(24)
                .repeatForever();

        return TriggerBuilder.newTrigger()
                .forJob(refreshTokenCleanupJobDetail())
                .withIdentity("refreshTokenCleanupTrigger")
                .withSchedule(scheduleBuilder)
                .startAt(DateBuilder.tomorrowAt(2, 0, 0))
                .build();
    }
}

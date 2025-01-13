package ru.hits.attackdefenceplatform.core.competition.configuration;

import lombok.extern.slf4j.Slf4j;
import org.quartz.DateBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import ru.hits.attackdefenceplatform.core.competition.CompetitionService;
import ru.hits.attackdefenceplatform.core.competition.job.CompetitionChangeStatusJob;
import ru.hits.attackdefenceplatform.core.token.job.RefreshTokenCleanupJob;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

@Configuration
@Slf4j
public class QuartzCompetitionConfiguration {

    @Bean
    public JobDetail competitionJobDetail() {
        return JobBuilder.newJob(CompetitionChangeStatusJob.class)
                .withIdentity("competitionChangeStatusJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger competitionJobTrigger(JobDetail competitionJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(competitionJobDetail)
                .withIdentity("competitionChangeStatusJobTrigger")
                .withSchedule(
                        SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInMinutes(1)
                                .repeatForever()
                )
                .build();
    }

    @Bean
    public JobDetail refreshTokenCleanupJobDetail() {
        return JobBuilder.newJob(RefreshTokenCleanupJob.class)
                .withIdentity("refreshTokenCleanupJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger refreshTokenCleanupTrigger() {
        var scheduleBuilder = SimpleScheduleBuilder
                .simpleSchedule()
                .withIntervalInHours(24)
                .repeatForever();

        return TriggerBuilder.newTrigger()
                .forJob(refreshTokenCleanupJobDetail())
                .withIdentity("refreshTokenCleanupTrigger")
                .withSchedule(scheduleBuilder)
                .startNow()
                .build();
    }

    @Bean
    public SpringBeanJobFactory springBeanJobFactory(ApplicationContext applicationContext) {
        var jobFactory = new SpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(
            SpringBeanJobFactory jobFactory,
            JobDetail competitionJobDetail,
            Trigger competitionJobTrigger
    ) {
        var factoryBean = new SchedulerFactoryBean();
        factoryBean.setJobFactory(jobFactory);
        factoryBean.setJobDetails(competitionJobDetail);
        factoryBean.setTriggers(competitionJobTrigger);
        return factoryBean;
    }

    @Bean
    public Scheduler scheduler(SchedulerFactoryBean schedulerFactoryBean) throws SchedulerException {
        var scheduler = schedulerFactoryBean.getScheduler();
        scheduler.start();
        return scheduler;
    }
}



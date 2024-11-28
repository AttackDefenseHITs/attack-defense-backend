package ru.hits.attackdefenceplatform.core.competition.configuration;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import ru.hits.attackdefenceplatform.core.competition.job.CompetitionCompletionJob;
import ru.hits.attackdefenceplatform.core.competition.job.CompetitionStartJob;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

@Configuration
public class QuartzCompetitionConfiguration {

    @Bean
    public JobDetail competitionJobDetail() {
        return JobBuilder.newJob(CompetitionStartJob.class)
                .withIdentity("competitionStartJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger competitionJobTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(competitionJobDetail())
                .withIdentity("competitionStartJobTrigger")
                .withSchedule(simpleSchedule()
                        .withIntervalInMinutes(1)
                        .repeatForever())
                .build();
    }

    @Bean
    public JobDetail competitionCompletionJobDetail() {
        return JobBuilder.newJob(CompetitionCompletionJob.class)
                .withIdentity("competitionCompletionJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger competitionCompletionJobTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(competitionCompletionJobDetail())
                .withIdentity("competitionCompletionJobTrigger")
                .withSchedule(simpleSchedule()
                        .withIntervalInMinutes(1)
                        .repeatForever())
                .build();
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        return new SchedulerFactoryBean();
    }
}


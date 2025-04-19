package com.basic.springpratice.domain.schedul.job;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

@Setter
@DisallowConcurrentExecution
@Slf4j
public class CronJob extends QuartzJobBean {


    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.info("TEST");
    }
}

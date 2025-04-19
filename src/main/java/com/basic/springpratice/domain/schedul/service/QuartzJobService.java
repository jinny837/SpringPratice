package com.basic.springpratice.domain.schedul.service;

import com.basic.springpratice.domain.schedul.dto.JobRequest;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuartzJobService {
    private final Scheduler scheduler;

    /** 1) 잡 추가 (등록 + 스케줄링) */
    public void scheduleJob(JobRequest req) throws Exception {
        // 1. JobDetail 생성
        Class<? extends Job> jobClazz =
                (Class<? extends Job>) Class.forName(req.getJobClass());
        JobDetail jobDetail = JobBuilder.newJob(jobClazz)
                .withIdentity(req.getJobName(), req.getJobGroup())
                .usingJobData(new JobDataMap(req.getJobData()))
                .build();

        // 2. Trigger 생성
        CronScheduleBuilder scheduleBuilder =
                CronScheduleBuilder.cronSchedule(req.getCronExpression());
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(req.getJobName() + "Trigger", req.getJobGroup())
                .withSchedule(scheduleBuilder)
                .forJob(jobDetail)
                .build();

        // 3. 스케줄러에 등록
        if (scheduler.checkExists(jobDetail.getKey())) {
            throw new SchedulerException("Job already exists: " + jobDetail.getKey());
        }
        scheduler.scheduleJob(jobDetail, trigger);
    }

    /** 2) 잡 삭제 */
    public boolean deleteJob(String name, String group) throws SchedulerException {
        JobKey key = JobKey.jobKey(name, group);
        return scheduler.deleteJob(key);
    }

    /** 3) 잡 일시정지 */
    public void pauseJob(String name, String group) throws SchedulerException {
        scheduler.pauseJob(JobKey.jobKey(name, group));
    }

    /** 4) 잡 재개 */
    public void resumeJob(String name, String group) throws SchedulerException {
        scheduler.resumeJob(JobKey.jobKey(name, group));
    }
}
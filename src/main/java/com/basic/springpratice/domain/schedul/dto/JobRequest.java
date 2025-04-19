package com.basic.springpratice.domain.schedul.dto;

import lombok.Data;
import lombok.ToString;

import java.util.Map;

@Data
@ToString
public class JobRequest {
    private String jobName;
    private String jobGroup = "DEFAULT";
    private String cronExpression;       // ex) "0/30 * * * * ?"
    private String jobClass;             // ex) "com.example.jobs.HelloJob"
    private Map<String, Object> jobData; // 선택적 JobDataMap
    // getters/setters
}

package com.basic.springpratice.domain.schedul.controller;

import com.basic.springpratice.domain.schedul.dto.JobRequest;
import com.basic.springpratice.domain.schedul.service.QuartzJobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.quartz.SchedulerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class QuartzJobController {
    private final QuartzJobService jobService;

    @PostMapping
    public ResponseEntity<String> createJob(@RequestBody @Valid JobRequest req) {
        try {
            jobService.scheduleJob(req);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Job scheduled: " + req.getJobName());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error scheduling job: " + e.getMessage());
        }
    }

    @DeleteMapping("/{group}/{name}")
    public ResponseEntity<String> deleteJob(
            @PathVariable String group,
            @PathVariable String name) throws SchedulerException {
        boolean deleted = jobService.deleteJob(name, group);
        if (deleted) {
            return ResponseEntity.ok("Job deleted");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Job not found");
        }
    }

    @PostMapping("/{group}/{name}/pause")
    public ResponseEntity<String> pauseJob(
            @PathVariable String group,
            @PathVariable String name) throws SchedulerException {
        jobService.pauseJob(name, group);
        return ResponseEntity.ok("Job paused");
    }

    @PostMapping("/{group}/{name}/resume")
    public ResponseEntity<String> resumeJob(
            @PathVariable String group,
            @PathVariable String name) throws SchedulerException {
        jobService.resumeJob(name, group);
        return ResponseEntity.ok("Job resumed");
    }
}

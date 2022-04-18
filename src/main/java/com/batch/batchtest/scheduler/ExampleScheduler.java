package com.batch.batchtest.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@EnableScheduling
@RequiredArgsConstructor
@Component
public class ExampleScheduler {

    private final Job job;
    private final JobLauncher jobLauncher;

    @Scheduled(fixedDelay =  30000)
    public void startJob(){
        try{
            Map<String, JobParameter> jobParameterMap = new HashMap<>();

            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM dd HH:mm:ss");
            Date time = new Date();

            String time1 = format1.format(time);

            jobParameterMap.put("requestDate" , new JobParameter(time1));

            JobParameters parameter = new JobParameters(jobParameterMap);

            JobExecution jobExecution = jobLauncher.run(job, parameter);

            while (jobExecution.isRunning()){
                log.info("is Running-----");
            }

        } catch (JobInstanceAlreadyCompleteException e) {
            throw new RuntimeException(e);
        } catch (JobExecutionAlreadyRunningException e) {
            throw new RuntimeException(e);
        } catch (JobParametersInvalidException e) {
            throw new RuntimeException(e);
        } catch (JobRestartException e) {
            throw new RuntimeException(e);
        }
    }
}

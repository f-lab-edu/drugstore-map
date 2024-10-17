package org.healthmap.config;

import lombok.RequiredArgsConstructor;
import org.healthmap.dto.BasicInfoDto;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BasicInfoBatchConfig {
    @Bean
    public Job basicInfoJob(JobRepository jobRepository, Step basicInfoStep) {
        return new JobBuilder("basicInfoJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(basicInfoStep)
                .build();
    }

    @Bean
    public Step basicInfoStep(JobRepository jobRepository,
                              PlatformTransactionManager transactionManager,
                              ItemReader<BasicInfoDto> itemReader,
                              ItemWriter<BasicInfoDto> itemWriter) {
        return new StepBuilder("basicInfoStep", jobRepository)
                .<BasicInfoDto, BasicInfoDto>chunk(100, transactionManager)
                .reader(itemReader)
                .writer(itemWriter)
                .faultTolerant()
                .retry(Exception.class)
                .retryLimit(2)
                .build();
    }
}

package org.healthmap.config;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.healthmap.writer.DetailInfoItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

//@Configuration
@RequiredArgsConstructor
public class DetailInfoBatchConfig {
    private final KafkaProperties kafkaProperties;
    private final DetailInfoItemWriter detailInfoItemWriter;

    @Bean
    public Job detailInfoJob(JobRepository jobRepository, Step detailInfoStep) {
        return new JobBuilder("detailInfoJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(detailInfoStep)
                .build();
    }

    @Bean
    public Step detailInfoStep(JobRepository jobRepository,
                               PlatformTransactionManager transactionManager,
                               ItemReader<String> itemReader) {
        return new StepBuilder("detailInfoStep", jobRepository)
                .<String, String>chunk(100, transactionManager)
                .reader(itemReader)
                .writer(detailInfoItemWriter)
                .faultTolerant()
                .retry(Exception.class)  // 모든 예외에 대해 재시도
                .retryLimit(2)           // 최대 2회 재시도
                .build();
    }

    @Bean
    public ItemReader<String> detailInfoItemReader(EntityManagerFactory entityManagerFactory) {
        return new JpaCursorItemReaderBuilder<String>()
                .name("detailInfoJpaCursorItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT m.id FROM MedicalFacilityEntity m")
                .build();
    }
}

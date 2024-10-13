package org.healthmap.config;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.kafka.builder.KafkaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Configuration
@RequiredArgsConstructor
public class DetailInfoConfig {
    private final KafkaProperties kafkaProperties;

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
                               ItemReader<String> itemReader,
                               ItemWriter<String> itemWriter) {
        return new StepBuilder("detailInfoStep", jobRepository)
                .<String, String>chunk(100, transactionManager)
                .reader(itemReader)
                .writer(itemWriter)
                .faultTolerant()
                .retry(Exception.class)  // 모든 예외에 대해 재시도
                .retryLimit(2)           // 최대 2회 재시도
                .build();
    }

    @Bean
    public ItemReader<String> detailInfoItemWriter(EntityManagerFactory entityManagerFactory){
        return new JpaCursorItemReaderBuilder<String>()
                .name("detailInfoJpaCursorItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT m.id FROM MedicalFacilityEntity m")
                .build();
    }

    @Bean
    public ItemWriter<String> itemWriter(KafkaTemplate<String, String> kafkaTemplate) {
        return new ItemWriter<String>() {
            @Override
            public void write(Chunk<? extends String> chunk) throws Exception {
                List<CompletableFuture<Void>> futures = new ArrayList<>();
                for(String id : chunk.getItems()) {
                    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                        kafkaTemplate.send(kafkaProperties.getTopic(), id);
                    });
                    futures.add(future);
                }
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            }
        };
    }
}

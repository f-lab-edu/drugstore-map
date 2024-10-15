package org.healthmap.config;

import lombok.RequiredArgsConstructor;
import org.healthmap.dto.BasicInfoDto;
import org.healthmap.openapi.dto.MedicalFacilityDto;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
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
                              ItemReader<MedicalFacilityDto> itemReader,
                              ItemProcessor<MedicalFacilityDto, BasicInfoDto> itemProcessor,
                              ItemWriter<BasicInfoDto> itemWriter) {
        return new StepBuilder("basicInfoStep", jobRepository)
                .<MedicalFacilityDto, BasicInfoDto>chunk(100, transactionManager)
                .reader(itemReader)
                .processor(itemProcessor)
                .writer(itemWriter)
                .faultTolerant()
                .retry(Exception.class)
                .retryLimit(2)
                .build();
    }

    //TODO: 나중에 BasicInfoDto로 통합하게되면 삭제
    @Bean
    public ItemProcessor<MedicalFacilityDto, BasicInfoDto> basicInfoProcessor() {
        return dto -> {
            String wtkCoordinate = dto.getCoordinate().toText();
            return new BasicInfoDto(dto.getCode(), dto.getName(), dto.getAddress(), dto.getPhoneNumber(), dto.getPageUrl(),
                    dto.getPostNumber(), dto.getType(), dto.getState(), dto.getCity(), dto.getTown(), wtkCoordinate);
        };
    }

}

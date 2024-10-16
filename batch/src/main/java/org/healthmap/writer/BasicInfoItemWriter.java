package org.healthmap.writer;

import lombok.RequiredArgsConstructor;
import org.healthmap.config.KafkaProperties;
import org.healthmap.dto.BasicInfoDto;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class BasicInfoItemWriter implements ItemWriter<BasicInfoDto> {
    private final KafkaTemplate<String, BasicInfoDto> kafkaTemplate;
    private final KafkaProperties kafkaProperties;

    @Override
    public void write(Chunk<? extends BasicInfoDto> chunk) throws Exception {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (BasicInfoDto dto : chunk.getItems()) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                kafkaTemplate.send(kafkaProperties.getBasicTopic(), dto);
            });
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }
}

package org.healthmap.writer;

import lombok.RequiredArgsConstructor;
import org.healthmap.config.KafkaProperties;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class DetailInfoItemWriter implements ItemWriter<String> {
    private final KafkaProperties kafkaProperties;
    private final KafkaTemplate<String, String> kafkaTemplate;

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
}

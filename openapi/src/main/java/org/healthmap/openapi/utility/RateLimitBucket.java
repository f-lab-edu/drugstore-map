package org.healthmap.openapi.utility;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class RateLimitBucket {
    private final Bucket bucket;

    public RateLimitBucket() {
        Bandwidth bandwidth = Bandwidth.builder()
                .capacity(30)
                .refillIntervally(30, Duration.ofSeconds(1))
                .build();
        this.bucket = Bucket.builder()
                .addLimit(bandwidth)
                .build();
    }

    public void consumeWithBlock(int numTokens) throws InterruptedException {
        bucket.asBlocking().consume(numTokens);
    }

}

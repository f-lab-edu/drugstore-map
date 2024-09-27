package org.healthmap.openapi.utility;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RateLimitBucket {
    private final Bucket bucket;

    public RateLimitBucket() {
        Bandwidth bandwidth = Bandwidth.builder().capacity(25)
                .refillGreedy(25, Duration.ofSeconds(1)).build();
        this.bucket = Bucket.builder()
                .addLimit(bandwidth).build();
    }

    public void consumeWithBlock(int numTokens) throws InterruptedException {
        bucket.asBlocking().consume(numTokens);
    }
}

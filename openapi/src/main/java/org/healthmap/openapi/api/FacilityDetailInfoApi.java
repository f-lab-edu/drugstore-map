package org.healthmap.openapi.api;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.healthmap.openapi.config.KeyProperties;
import org.healthmap.openapi.config.UrlProperties;
import org.healthmap.openapi.dto.FacilityDetailDto;
import org.healthmap.openapi.error.OpenApiErrorCode;
import org.healthmap.openapi.exception.OpenApiProblemException;
import org.healthmap.openapi.pattern.PatternMatcherManager;
import org.healthmap.openapi.utility.RateLimitBucket;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class FacilityDetailInfoApi {
    private final KeyProperties keyProperties;
    private final UrlProperties urlProperties;
    private final PatternMatcherManager patternMatcherManager;
    private final ObjectMapper objectMapper;
    private final RateLimitBucket rateLimitBucket;
    private final ExecutorService executorService;
    private final HttpClient client;

    public FacilityDetailInfoApi(KeyProperties keyProperties, UrlProperties urlProperties, PatternMatcherManager patternMatcherManager, RateLimitBucket rateLimitBucket) {
        this.keyProperties = keyProperties;
        this.urlProperties = urlProperties;
        this.patternMatcherManager = patternMatcherManager;
        this.rateLimitBucket = rateLimitBucket;
        this.objectMapper = new ObjectMapper();
        this.executorService = Executors.newFixedThreadPool(10);
        this.client = HttpClient.newBuilder().build();
    }

    // OpenApi로부터 데이터 받아오는 역할만 부여
    // TODO: consumer 사용시 변경 해야함
    public CompletableFuture<FacilityDetailDto> getFacilityDetailDtoFromApi(String code, Queue<String> idQueue) {
        String apiUrl = urlProperties.getDetailUrl()
                + "?serviceKey=" + keyProperties.getServerKey()    //Service Key
                + "&ykiho=" + code
                + "&_type=json";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .GET()
                .build();

        return CompletableFuture.supplyAsync(() -> {
                    try {
                        rateLimitBucket.consumeWithBlock(1);
                        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                    } catch (InterruptedException e) {
                        log.error(e.getMessage());
                        throw new RuntimeException("Rate limit exceeded", e);
                    }
                }, executorService).thenCompose(future -> future)
                .thenApply(HttpResponse::body)
                .thenApply(this::getFacilityDetailJsonDto)
                .thenApply(x -> {
                    if (x != null) {
                        x.saveCodeIntoDto(code);
                    }
                    return x;
                })
                .exceptionally(ex -> {
                    if (ex.getMessage().contains("LIMITED_NUMBER_OF_SERVICE_REQUESTS_PER_SECOND_EXCEEDS_ERROR")) {
                        log.warn("초당 요청 한도 초과, 재시도...");
                        // 재시도 로직 또는 지연 후 재시도
                        idQueue.add(code);
                        return null;
                    }
                    log.error("error 발생, null 반환: {}", ex.getMessage());
                    return null;
                });
    }


    private FacilityDetailDto getFacilityDetailJsonDto(String jsonBody) {
        FacilityDetailDto facilityDetailDto = null;
        try {
            JsonNode path = objectMapper.readTree(jsonBody)
                    .path("response")
                    .path("body")
                    .path("items");
            if (!path.isEmpty()) {
                JsonNode itemNode = path.path("item");
                facilityDetailDto = objectMapper.treeToValue(itemNode, FacilityDetailDto.class);
            }
        } catch (JsonParseException je) {
            throw new OpenApiProblemException(OpenApiErrorCode.SERVER_ERROR, "LIMITED_NUMBER_OF_SERVICE_REQUESTS_PER_SECOND_EXCEEDS_ERROR");
        } catch (Exception e) {  // return null 할지 생각
            log.error("getFacilityDetailJson error : {}", e.getMessage(), e);
            throw new OpenApiProblemException(OpenApiErrorCode.SERVER_ERROR, e.getMessage());
        }
        return facilityDetailDto;
    }
}

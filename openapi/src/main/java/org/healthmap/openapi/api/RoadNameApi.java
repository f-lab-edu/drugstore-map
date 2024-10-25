package org.healthmap.openapi.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.healthmap.openapi.config.KeyProperties;
import org.healthmap.openapi.config.UrlProperties;
import org.healthmap.openapi.error.MapApiErrorCode;
import org.healthmap.openapi.exception.MapApiProblemException;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

// 도로명 주소 API
@Component
@RequiredArgsConstructor
@Slf4j
public class RoadNameApi {
    private final KeyProperties keyProperties;
    private final UrlProperties urlProperties;

    public String getNewAddressFromApi(String address) {
        String result = null;
        StringBuilder urlStr = new StringBuilder(urlProperties.getRoadAddressUrl())
                .append("?confmKey=" + keyProperties.getRoadAddressKey())
                .append("&resultType=json")
                .append("&hstryYn=Y")
                .append("&keyword=")
                .append(URLEncoder.encode(address, StandardCharsets.UTF_8));
        try {
            URL url = new URL(urlStr.toString());
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url.toURI())
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
            JsonObject asJsonObject = jsonObject.get("results").getAsJsonObject();
            JsonArray documents = asJsonObject.getAsJsonArray("juso");
            if (!documents.isEmpty()) {
                JsonObject jsonObj = documents.get(0).getAsJsonObject();
                result = getStringFromJson(jsonObj, "roadAddrPart1");
            }
        } catch (Exception e) {
            throw new MapApiProblemException(MapApiErrorCode.SERVER_ERROR);
        }
        return result;
    }

    private String getStringFromJson(JsonObject jsonObject, String tagName) {
        if (jsonObject.has(tagName)) {
            return jsonObject.get(tagName).getAsString();
        } else {
            return null;
        }
    }
}

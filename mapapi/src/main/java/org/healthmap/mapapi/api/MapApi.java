package org.healthmap.mapapi.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.healthmap.mapapi.config.KeyProperties;
import org.healthmap.mapapi.config.UrlProperties;
import org.healthmap.mapapi.error.MapApiErrorCode;
import org.healthmap.mapapi.exception.MapApiProblemException;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MapApi {
    private final KeyProperties keyProperties;
    private final UrlProperties urlProperties;

    public List<Double> getCoordinateFromMapApi(String address) {
        List<Double> coordinateList = new ArrayList<>();        //x, y 순서로 저장
        StringBuilder urlStr = new StringBuilder(urlProperties.getMapAddressUrl())
                .append("?query=")
                .append(URLEncoder.encode(address, StandardCharsets.UTF_8));
        try {
            URL url = new URL(urlStr.toString());
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .setHeader("Authorization", "KakaoAK " + keyProperties.getKakaoKey())
                    .uri(url.toURI())
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();

            JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
            JsonArray documents = jsonObject.getAsJsonArray("documents");
            if (!documents.isEmpty()) {
                JsonObject jsonObj = documents.get(0).getAsJsonObject();
                Double x = getDoubleFromJson(jsonObj, "x");
                Double y = getDoubleFromJson(jsonObj, "y");
                coordinateList.add(x);
                coordinateList.add(y);
            }
        } catch (Exception e) {
            throw new MapApiProblemException(MapApiErrorCode.SERVER_ERROR);
        }
        return coordinateList;
    }

    public Double getDoubleFromJson(JsonObject jsonObject, String tagName) {
        if (jsonObject.has(tagName)) {
            return jsonObject.get(tagName).getAsDouble();
        } else {
            return null;
        }
    }
}

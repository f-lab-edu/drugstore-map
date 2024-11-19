package org.healthmap.dto.serializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.io.IOException;

public class GeoJsonPointDeserializer extends JsonDeserializer<GeoJsonPoint> {
    @Override
    public GeoJsonPoint deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);
        double x = jsonNode.get("x").asDouble();
        double y = jsonNode.get("y").asDouble();
        return new GeoJsonPoint(x, y);
    }
}

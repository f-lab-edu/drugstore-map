package org.healthmap.dto.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.io.IOException;

public class GeoJsonPointSerializer extends JsonSerializer<GeoJsonPoint> {

    @Override
    public void serialize(GeoJsonPoint geoJsonPoint, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("x", geoJsonPoint.getX());
        jsonGenerator.writeNumberField("y", geoJsonPoint.getY());
        jsonGenerator.writeEndObject();
    }
}

package org.healthmap.dto.serializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import java.io.IOException;

// Point 타입 Deserializer
public class PointDeserializer extends JsonDeserializer<Point> {
    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Override
    public Point deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);
        double x = jsonNode.get("x").asDouble();
        double y = jsonNode.get("y").asDouble();
        Point point = geometryFactory.createPoint(new Coordinate(x, y));
        point.setSRID(4326);

        return point;
    }
}

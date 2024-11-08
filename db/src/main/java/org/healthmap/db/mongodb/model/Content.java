package org.healthmap.db.mongodb.model;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "test")
@AllArgsConstructor
public class Content {
    @Id
    private String id;
    private String name;
    private Integer age;

}

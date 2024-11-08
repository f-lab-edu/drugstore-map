package org.healthmap.db.mongodb.repository;

import org.healthmap.db.mongodb.model.Content;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContentRepository extends MongoRepository<Content, String> {
}

package org.healthmap.db.mongodb.repository;

import org.healthmap.db.mongodb.model.MedicalFacility;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MedicalFacilityMongoRepository extends MongoRepository<MedicalFacility, String> {


}

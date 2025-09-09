package com.brenda.clexis.clientGatewayService.repository;

import com.brenda.clexis.clientGatewayService.model.entity.LearningPath;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LearningPathRepository extends MongoRepository<LearningPath, String> {
    boolean existsLearningPathByActiveIsTrue();
    LearningPath findLearningPathByActiveIsTrueAndUserId(String userId);

    List<LearningPath> findLearningPathsByUserId(String userId);
}

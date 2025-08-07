package com.brenda.clexis.clientGatewayService.repository;

import com.brenda.clexis.clientGatewayService.model.entity.LearningAssets;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LearningAssetsRepository extends MongoRepository<LearningAssets, String> {
}

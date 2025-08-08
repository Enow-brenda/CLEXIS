package com.brenda.clexis.clientGatewayService.repository;

import com.brenda.clexis.clientGatewayService.model.entity.LearningAssets;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LearningAssetsRepository extends MongoRepository<LearningAssets, String> {
    List<LearningAssets> findLearningAssetsByUserId(String userId);
}

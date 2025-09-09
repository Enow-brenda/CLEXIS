package com.brenda.clexis.clientGatewayService.repository;

import com.brenda.clexis.clientGatewayService.model.entity.Discussion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface DiscussionRepository extends MongoRepository<Discussion, String> {
    Discussion findDiscussionById(String id);

    @Query("{ '$or': [ { 'userId': ?0 }, { 'responses.userId': ?0 } ] }")
    List<Discussion> findByAuthorIdOrResponseUserId(String userId);

}

package com.brenda.clexis.clientGatewayService.repository;

import com.brenda.clexis.clientGatewayService.model.entity.BuddyProgram;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface BuddyProgramRepository extends MongoRepository<BuddyProgram, String> {
    BuddyProgram findBuddyProgramsById(String id);
}

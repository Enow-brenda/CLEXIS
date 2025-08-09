package com.brenda.clexis.notificationService.repository;


import com.brenda.clexis.notificationService.models.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    User findUserById(String id);
}

package com.brenda.clexis.clientGatewayService.repository;


import com.brenda.clexis.clientGatewayService.model.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User findUserByUsername(String username);
    User findUserByEmail(String email);

    @Query("{ '$or': [ { 'username': ?0 }, { 'email': ?0 } ] }")
    User findUserByUsernameOrEmail(String usernameOrEmail);

    boolean existsUserByEmail(String email);

    boolean existsUserByUsername(String username);
}

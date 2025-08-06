package com.brenda.clexis.clientGatewayService.repository;


import com.brenda.clexis.clientGatewayService.model.entity.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends MongoRepository<Role, String> {
    Role findRoleByName(String name);
    boolean existsRoleByName(String name);
}

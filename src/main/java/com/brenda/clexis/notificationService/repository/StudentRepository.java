package com.brenda.clexis.notificationService.repository;


import com.brenda.clexis.notificationService.models.entity.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends MongoRepository<Student, String> {
    Student findStudentByUserId(String id);
}

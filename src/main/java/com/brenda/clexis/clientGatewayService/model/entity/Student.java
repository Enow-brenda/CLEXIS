package com.brenda.clexis.clientGatewayService.model.entity;

import com.brenda.clexis.clientGatewayService.model.enums.AcademicLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "students")
public class Student {
    @Id
    private String id;

    @Indexed(unique = true)
    private String userId;
    private String fullName;
    private AcademicLevel academicLevel;
    private String bioOrInterest;
    private String phoneNumber;
    private String profession;
    private String language;
    private int points;
    private int resourcePoints;
    private int communityPoints;
    private String dateCreated;

}

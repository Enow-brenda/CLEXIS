package com.brenda.clexis.clientGatewayService.model.dto.application;


import com.brenda.clexis.clientGatewayService.model.enums.AcademicLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentDto {

    private String fullName;
    private AcademicLevel academicLevel;
    private String password;
    private String email;
    private String phone;
    private String bioOrInterest;
    private String profession;
    private String language;
}

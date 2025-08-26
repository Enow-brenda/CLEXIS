package com.example.clexis.models.request;

public class RegisterRequest {
    private String fullName;
    private String academicLevel;
    private String password;
    private String email;
    private String phone;
    private String bioOrInterest;
    private String profession;
    private String language;

    public RegisterRequest(String fullName, String academicLevel, String password,
                           String email, String phone, String bioOrInterest,
                           String profession, String language) {
        this.fullName = fullName;
        this.academicLevel = academicLevel;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.bioOrInterest = bioOrInterest;
        this.profession = profession;
        this.language = language;
    }

    public String getPassword(){
        return this.password;
    }
    public String getEmail(){
        return this.email;
    }
}

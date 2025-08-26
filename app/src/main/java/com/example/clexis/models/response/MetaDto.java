package com.example.clexis.models.response;

public class MetaDto {
    public int statusCode ;
    public String statusDescription ;
    public String message ;

    public String getMessage(){
        return  this.message;
    }

    public int getStatusCode(){
        return  this.statusCode;
    }
}

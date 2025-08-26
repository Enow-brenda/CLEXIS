package com.example.clexis.models.entity;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class User extends RealmObject {

    @PrimaryKey
    private String id;

    private String username;


    private String email;
    private String password;
    private String role;
    private boolean blocked = false;

}

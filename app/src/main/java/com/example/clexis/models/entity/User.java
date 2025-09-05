package com.example.clexis.models.entity;


import io.realm.annotations.PrimaryKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class User{

    @PrimaryKey
    private String id;

    private String username;


    private String email;
    private String password;
    private String role;
    private boolean blocked = false;

    public static User defaultUser() {
        User user = new User();
        user.id = "USR67890";
        user.username = "johndoe";
        user.email = "johndoe@example.com";
        user.password = "password123"; // ⚠️ only for testing, never in real use
        user.role = "STUDENT";         // or "ADMIN", depending on your roles
        user.blocked = false;
        return user;
    }

}

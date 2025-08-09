package com.brenda.clexis.notificationService.models.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "users")
public class User{
    @Id
    private String id;
    @Indexed(unique = true)
    private String username;

    @Indexed(unique = true)
    private String email;
    private String password;
    private String role;
    private boolean blocked = false;

}

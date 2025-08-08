package com.brenda.clexis.clientGatewayService.model.entity;

import com.brenda.clexis.clientGatewayService.model.enums.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document(collection = "resources")
public class Resource {

    @Id
    private String id = UUID.randomUUID().toString();
    private String name;
    private String description;
    private ResourceType type;
    private boolean verified;
    private boolean free;
    private int price;
    private String merchantNumber;
    private String fileUrl;
    private String imageUrl;
    private String userId;
    private LocalDateTime creationDate = LocalDateTime.now();
}

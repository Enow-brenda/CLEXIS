package com.brenda.clexis.clientGatewayService.model.dto;

import com.brenda.clexis.clientGatewayService.model.enums.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResourceDto {
    private String name;
    private String description;
    private ResourceType type;
    private boolean free;
    private int price;
    private String merchantNumber;
    private String fileUrl;
    private String imageUrl;
}

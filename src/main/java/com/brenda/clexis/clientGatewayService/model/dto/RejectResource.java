package com.brenda.clexis.clientGatewayService.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class RejectResource {
    private String resourceId;
    private String reason;
}

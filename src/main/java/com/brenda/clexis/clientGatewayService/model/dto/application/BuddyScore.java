package com.brenda.clexis.clientGatewayService.model.dto.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class BuddyScore {
    private String userId;
    private String username;
    private int score;
}

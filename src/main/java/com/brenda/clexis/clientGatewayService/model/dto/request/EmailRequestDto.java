package com.brenda.clexis.clientGatewayService.model.dto.request;


import com.brenda.clexis.clientGatewayService.model.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailRequestDto {
    private NotificationType messageTag;
    private List<String> recipients;
    List<String> params;
}

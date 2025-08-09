package com.brenda.clexis.clientGatewayService.model.dto;

import com.brenda.clexis.clientGatewayService.model.enums.DiscussionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiscussionDto {
    private String discussionTitle;
    private String discussionBody;
    private DiscussionType type;
}

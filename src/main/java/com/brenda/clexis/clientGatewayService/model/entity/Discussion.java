package com.brenda.clexis.clientGatewayService.model.entity;

import com.brenda.clexis.clientGatewayService.model.dto.application.Review;
import com.brenda.clexis.clientGatewayService.model.enums.DiscussionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document(collection = "discussions")
public class Discussion {
    @Id
    private String id = UUID.randomUUID().toString();
    private String discussionTitle;
    private String discussionBody;
    private String userId;
    private List<Review> responses;
    private String date;
    private DiscussionType type;
}

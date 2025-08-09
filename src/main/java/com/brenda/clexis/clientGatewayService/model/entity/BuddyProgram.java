package com.brenda.clexis.clientGatewayService.model.entity;

import com.brenda.clexis.clientGatewayService.model.dto.application.BuddyScore;
import com.brenda.clexis.clientGatewayService.model.dto.application.Milestone;
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
@Document(collection = "buddyPrograms")
public class BuddyProgram {
    @Id
    private String id = UUID.randomUUID().toString();
    private String title;
    private String description;
    private String deadline;
    private boolean opened;
    private String authorId;
    private List<Milestone> mileStoneList;
    private List<BuddyScore> buddies;
}

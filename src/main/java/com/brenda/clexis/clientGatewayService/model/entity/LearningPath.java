package com.brenda.clexis.clientGatewayService.model.entity;

import com.brenda.clexis.clientGatewayService.model.dto.application.Task;
import com.brenda.clexis.clientGatewayService.model.enums.GoalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document(collection = "learningPaths")
public class LearningPath {
    @Id
    private String id= UUID.randomUUID().toString();
    private String name;
    private String description;
    private GoalType goalType;
    private List<Task> frequentTasks;
    private List<Module> modules;
    private String endDate;
    private String startDate;
    private boolean active;
    private String userId;
    private LocalDateTime creationDate;
}

package com.brenda.clexis.clientGatewayService.model.dto;

import com.brenda.clexis.clientGatewayService.model.enums.GoalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LearningPathDto {
    private String name;
    private String description;
    private GoalType goalType;
    private List<FrequentTaskDto> frequentTasks;
    private List<ModuleDto> modules;
    private String endDate;
    private String startDate;
}

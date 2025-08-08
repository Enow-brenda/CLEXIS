package com.brenda.clexis.clientGatewayService.model.dto;

import com.brenda.clexis.clientGatewayService.model.dto.application.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModuleDto {
    private String moduleName;
    private String objective;
    private List<TaskDto> tasks;
}

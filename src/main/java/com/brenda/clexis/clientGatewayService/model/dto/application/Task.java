package com.brenda.clexis.clientGatewayService.model.dto.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Task {
    private String id = UUID.randomUUID().toString();
    private String title;
    private String description;
    private boolean completed = false;
    private boolean frequentTask = false;
    private String date;
    private boolean daily;
    private boolean weekly;
    private boolean monthly;
    private int frequency;
    private List<Integer> scheduledDays;

}

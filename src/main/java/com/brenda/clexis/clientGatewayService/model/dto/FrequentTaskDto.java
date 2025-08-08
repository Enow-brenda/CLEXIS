package com.brenda.clexis.clientGatewayService.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FrequentTaskDto {
    private String title;
    private String description;
    private boolean daily;
    private boolean weekly;
    private boolean monthly;
    private int frequency;
    private List<Integer> scheduledDays;
}

package com.brenda.clexis.notificationService.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomEmailRequestDto {
    private String heading;
    private String message;
    private List<String> recipients;
}

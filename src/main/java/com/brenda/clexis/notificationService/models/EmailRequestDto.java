package com.brenda.clexis.notificationService.models;

import com.brenda.clexis.notificationService.enums.NotificationType;
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

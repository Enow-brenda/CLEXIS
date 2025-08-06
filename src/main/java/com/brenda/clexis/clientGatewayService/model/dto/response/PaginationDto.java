package com.brenda.clexis.clientGatewayService.model.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PaginationDto {
    public int count;
    public int total;

    public PaginationDto() {
        this.count = 1;
        this.total = 1;
    }
}

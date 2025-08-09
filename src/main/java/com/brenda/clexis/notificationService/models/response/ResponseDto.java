package com.brenda.clexis.notificationService.models.response;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDto {

    public MetaDataDto meta;
    public Object data;
    public Object errors;
    public PaginationDto pagination;

    public ResponseDto(Object data) {
        this.data = data;
        this.meta = MetaDataDto.builder()
                .statusCode(200)
                .statusDescription("SUCCESSFUL")
                .message("successful")
                .build();
        this.pagination = new PaginationDto();
    }

    public ResponseDto(MetaDataDto meta) {
        this.meta = meta;
        this.pagination = new PaginationDto();
    }

}

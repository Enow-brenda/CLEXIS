package com.brenda.clexis.clientGatewayService.model.dto.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class MainResponse {

    public static ResponseEntity<ResponseDto> responseOk(Object data){
        return new ResponseEntity<>(new ResponseDto(data), HttpStatus.OK);
    }

    public static ResponseEntity<ResponseDto> responseInvalidMsisdn(String msisdn){
        MetaDataDto metaDto= MetaDataDto.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message("Invalid msisdn format")
                .statusDescription("Bad Request")
                .build();

        ResponseDto responseDto = ResponseDto.builder()
                .data(null)
                .errors("Parameter <msisdn> "+  msisdn + " must contain 9 digits (national) or 12 digits starting with 237.")
                .pagination(new PaginationDto())
                .meta(metaDto)
                .build();

        ResponseEntity responseEntity = new ResponseEntity(responseDto,HttpStatus.BAD_REQUEST);

        return responseEntity;
    }

    public static ResponseEntity<ResponseDto> responseOk(Object obj, String message) {

        MetaDataDto metaDto= MetaDataDto.builder()
                .statusCode(HttpStatus.OK.value())
                .message(message)
                .statusDescription("SUCCESSFUL")
                .build();

        ResponseDto responseDto = ResponseDto.builder()
                .data(obj)
                .errors(null)
                .pagination(new PaginationDto())
                .meta(metaDto)
                .build();

        ResponseEntity responseEntity = new ResponseEntity(responseDto,HttpStatus.OK);

        return responseEntity;
    }

    public static ResponseEntity<ResponseDto> response(Object data){
        if (data!=null)
            return responseOk(data);
        return responseNotFound("Entity not found");
    }

    public static ResponseEntity<ResponseDto> responseNotFound(String description){
        return responseNotFound(description, description);
    }

    public static ResponseEntity<ResponseDto> responseFailed(String description, String message){
        var meta = MetaDataDto.builder()
                .statusCode(1001)
                .statusDescription(description)
                .message(message)
                .build();
        return new ResponseEntity<>(new ResponseDto(meta), HttpStatus.OK);
    }
    public static ResponseEntity<ResponseDto> responseBadCredentials(Object data){
        var response = ResponseDto.builder()
                .data(data)
                .errors("Bad Credentials")
                .meta(MetaDataDto.builder()
                        .statusCode(400)
                        .statusDescription("FAILED")
                        .message("Bad Credentials")
                        .build())
                .pagination(new PaginationDto())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<ResponseDto> Unauthorized(Object data){
        var response = ResponseDto.builder()
                .data(data)
                .errors("Unauthorized")
                .meta(MetaDataDto.builder()
                        .statusCode(401)
                        .statusDescription("UNAUTHORIZED")
                        .message("Full authentication is required to access this resource")
                        .build())
                .pagination(new PaginationDto())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<ResponseDto> responseNotFound(String description, String message){
        var meta = MetaDataDto.builder()
                .statusCode(404)
                .statusDescription(description)
                .message(message)
                .build();
        return new ResponseEntity<>(new ResponseDto(meta), HttpStatus.OK);
    }

    public static ResponseEntity<ResponseDto> responseError(Object errors){

        var response = ResponseDto.builder()
                .data(null)
                .errors(errors)
                .meta(MetaDataDto.builder()
                        .statusCode(500)
                        .statusDescription("INTERNAL_SERVER_ERROR")
                        .message(errors.toString())
                        .build())
                .pagination(new PaginationDto())
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static ResponseEntity<ResponseDto> responseAlreadyExist(Object errors){
        var data = ResponseDto.builder()
                .data(null)
                .errors(errors)
                .meta(MetaDataDto.builder()
                        .statusCode(409)
                        .statusDescription("CONFLICT")
                        .message(errors.toString())
                        .build())
                .build();
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    public static ResponseEntity<ResponseDto> responseExpiredToken() {
        var meta = MetaDataDto.builder()
                .statusCode(410)
                .statusDescription("Gone")
                .message("Token Expired or Already Used")
                .build();
        var data = ResponseDto.builder()
                .errors("Token Expired or Already Used")
                .pagination(null)
                .meta(meta)
                .build();

        ResponseEntity responseEntity = new ResponseEntity(data,HttpStatus.GONE);
        return responseEntity;

    }

    public static ResponseEntity<ResponseDto> responseInvalidToken() {
        var meta = MetaDataDto.builder()
                .statusCode(404)
                .statusDescription("Not Found")
                .message("Not Found")
                .build();
        var data = ResponseDto.builder()
                .errors("Token is Invalid")
                .pagination(null)
                .meta(meta)
                .build();

        ResponseEntity responseEntity = new ResponseEntity(data,HttpStatus.NOT_FOUND);
        return responseEntity;


    }
}

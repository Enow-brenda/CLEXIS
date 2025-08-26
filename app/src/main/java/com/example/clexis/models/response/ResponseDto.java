package com.example.clexis.models.response;

public class ResponseDto<T> {
    public MetaDto meta;
    public T data;
    public String errors;
    public PaginationDto pagination;

    public T getData(){
        return this.data;
    }

    public MetaDto getMeta(){
        return this.meta;
    }

    public String getErrors(){
        return this.errors;
    }
}

package com.example.clexis.models.response;

public class PaginationDto {
    public int count;
    public int total;

    public PaginationDto() {
        this.count = 1;
        this.total = 1;
    }
}

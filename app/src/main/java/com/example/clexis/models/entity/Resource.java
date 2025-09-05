package com.example.clexis.models.entity;


import com.example.clexis.models.enums.ResourceType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class Resource  {


    private String id;
    private String name;
    private String description;
    private ResourceType type;
    private String category;
    private boolean verified;
    private boolean rejected ;
    private String reasonForRejection;
    private boolean free;
    private int price;
    private String merchantNumber;
    private String fileUrl;
    private String imageUrl;
    private String userId;
    private String username;
    private String creationDate;
}

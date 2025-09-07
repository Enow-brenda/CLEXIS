package com.brenda.clexis.clientGatewayService.model.enums;


public enum AcademicLevel {
    HIGHSCHOOL,
    UNDERGRADUATE,
    GRADUATE,
    OTHER;

    public int getCode() {
        switch (this) {
            case HIGHSCHOOL:
                return 0;
            case UNDERGRADUATE:
                return 1;
            case GRADUATE:
                return 2;
            case OTHER:
                return 3;
        }
        return 0;

    }



}


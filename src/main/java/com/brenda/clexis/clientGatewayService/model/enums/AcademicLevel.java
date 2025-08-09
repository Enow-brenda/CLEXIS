package com.brenda.clexis.clientGatewayService.model.enums;


public enum AcademicLevel {
    HIGHSCHOOL(1, "High School"),
    UNDERGRADUATE(2, "Undergraduate"),
    GRADUATE(3, "Graduate"),
    OTHER(99, "Other");

    private final int code;
    private final String label;

    AcademicLevel(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public int getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static AcademicLevel fromCode(int code) {
        for (AcademicLevel level : values()) {
            if (level.code == code) {
                return level;
            }
        }
        return OTHER;
    }
}


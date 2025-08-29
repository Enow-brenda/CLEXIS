package com.example.clexis.models;

public class Utils {
    public String getInitials(String fullName) {
        if (fullName == null || fullName.isEmpty()) return "";
        String[] parts = fullName.split(" ");
        String initials = "";
        for (String part : parts) {
            if (!part.isEmpty()) {
                initials += part.charAt(0);
            }
        }
        if(initials.length()>2){
            return initials.toUpperCase().substring(0,2);
        }
        return initials.toUpperCase();
    }
}

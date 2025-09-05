package com.example.clexis.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    public void sendNotifcation(String message, List<String> userIds){

    }
    public int getDayStatus(String start,String end){
        Date today = new Date();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        boolean beforeStart = false;
        boolean beforeEnd = false;
        try {
            // Parse start date
            Date startDate = sdf.parse(start);
            Date endDate = sdf.parse(end);

            // Compare
            if(today.before(startDate)) {
                beforeStart = true;
            } else if(today.before(endDate)) {
                beforeEnd = true;
            }

        } catch (
                ParseException e) {
            e.printStackTrace();
        }
        if(beforeStart){
            return 0;//start
        }
        else if(beforeEnd){
            return 1;//ongoing
        }else{
            return 2; //due
        }
    }


}

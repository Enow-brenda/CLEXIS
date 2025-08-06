package com.brenda.clexis.clientGatewayService.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author djoud
 * created 13/10/2024
 * Project theWinnerGateway
 **/

@Component
public class DateTimeUtils {

    public LocalDateTime currentDateTime(){
        return LocalDateTime.now();
    }

}

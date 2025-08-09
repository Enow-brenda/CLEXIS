package com.brenda.clexis.clientGatewayService.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author djoud
 * created 27/11/2023
 * Project UssdGav
 **/

@Slf4j
@RequiredArgsConstructor
@Component
public class Utils {
    private final ObjectMapper objectMapper;


    public Locale getLocaleByLanguage(String language){
        if (language==null || language.equals("0") || language.toUpperCase().equals("ENGLISH")){
            return  Locale.ENGLISH;
        }
        return Locale.FRENCH;
    }


    public <T> T convertDataObjectFromHashMap(Object data, Class<T> aClass) {
        return objectMapper.convertValue(data, aClass);
    }

    public <T> List<T> convertListDataObjectFromHashMap(Object data, Class<T> aClass) {
        var datas =  convertDataObjectFromHashMap(data, ArrayList.class);
        if (datas!=null){
            List<T> endTransactions = new ArrayList<>();
            datas.forEach(aData->endTransactions.add(convertDataObjectFromHashMap(aData, aClass)));
            return  endTransactions;
        }
        return List.of();
    }




    public String convertDataToJsonString(Object data){
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error("Error converting object {} to jsonString", data);
            throw new RuntimeException(e);
        }
    }

    public <T> T convertDataObjectFromJsonString(String data, Class<T> aClass) {
        try {
            return objectMapper.readValue(data, aClass);
        } catch (JsonProcessingException e) {
            log.error("Error converting data {} to class {}", data, aClass);
            throw new RuntimeException(e);
        }
    }

    public String doubleToStringAmount(double amount){
        return String.format("%.0f", amount);
    }
}

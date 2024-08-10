package com.dogejava.utils;

public class StringUtils {
    public static String upperCaseFirstLetter(String field){
        if(field.isEmpty()){
            return field;
        }
        return field.substring(0,1).toUpperCase()+field.substring(1);

    }

    public static String lowerCaseFirstLetter(String field){
        if(field.isEmpty()){
            return field;
        }
        return field.substring(0,1).toLowerCase()+field.substring(1);

    }

}

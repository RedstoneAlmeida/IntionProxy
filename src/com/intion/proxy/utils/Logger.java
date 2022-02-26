package com.intion.proxy.utils;

public class Logger {

    public static void log(String information)
    {
        System.out.println(information);
    }

    public static void log(String information, Exception e)
    {
        System.out.printf("%s | %s%n", information, e.getLocalizedMessage());
    }

}

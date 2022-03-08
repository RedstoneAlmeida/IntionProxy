package com.intion.proxy.utils;

public enum LoggerEnum {

    DEBUG("DEBUG"),
    ERROR("ERROR"),
    CRITICAL("CRITICAL"),
    INFO("INFO"),
    COMMAND("COMMAND"),
    PACKET("PACKET");

    String prefix;

    LoggerEnum(String prefix) {
        this.prefix = prefix;
    }

    public void log(String information)
    {
        Logger.log(String.format("[%s] %s", this.prefix, information));
    }

    public void log(String information, Exception e)
    {
        Logger.log(String.format("[%s] %s", this.prefix, information), e);
    }
}

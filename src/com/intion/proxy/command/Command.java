package com.intion.proxy.command;

import com.intion.proxy.Loader;

public abstract class Command {

    private String name;

    public void setName(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void init(Loader server)
    {

    }

    public void execute(Loader server)
    {
        this.execute(server, new String[0]);
    }

    public void execute(Loader server, String[] args)
    {

    }

}

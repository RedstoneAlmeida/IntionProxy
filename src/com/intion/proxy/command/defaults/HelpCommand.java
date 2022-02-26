package com.intion.proxy.command.defaults;

import com.intion.proxy.Loader;
import com.intion.proxy.command.Command;
import com.intion.proxy.utils.Logger;

public class HelpCommand extends Command {

    public HelpCommand() {
        this.setName("help");
    }

    @Override
    public void execute(Loader server, String[] args) {
        for (Command command : server.getCommandMap().getCommandMap().values())
        {
            Logger.log(command.getName());
        }
    }
}

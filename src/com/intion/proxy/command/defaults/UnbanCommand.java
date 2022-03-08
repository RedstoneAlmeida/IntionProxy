package com.intion.proxy.command.defaults;

import com.intion.proxy.Loader;
import com.intion.proxy.command.Command;
import com.intion.proxy.utils.IntionConfig;
import com.intion.proxy.utils.Logger;
import com.intion.proxy.utils.LoggerEnum;

public class UnbanCommand extends Command {

    private IntionConfig config;

    public UnbanCommand()
    {
        this.setName("unban");
    }

    @Override
    public void init(Loader server) {
        this.config = server.getBanConfig();
    }

    @Override
    public void execute(Loader server, String[] args) {
        if (args.length < 2)
            return;
        String playerName = args[1];
        if (playerName.contains("&"))
            playerName = playerName.replaceAll("&", " ");

        if (this.config.exist(playerName))
        {
            String xuid = (String) this.config.get(playerName);
            this.config.remove(xuid);
            this.config.remove(playerName);
            this.config.save();
            LoggerEnum.COMMAND.log(playerName + " is unbanned");
            return;
        }
        LoggerEnum.COMMAND.log("Not found " + playerName + ", try again.");
    }

}

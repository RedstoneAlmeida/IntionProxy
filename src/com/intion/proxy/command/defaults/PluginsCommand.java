package com.intion.proxy.command.defaults;

import com.intion.proxy.Loader;
import com.intion.proxy.command.Command;
import com.intion.proxy.microplugin.PluginLoader;
import com.intion.proxy.utils.Logger;

public class PluginsCommand extends Command {

    public PluginsCommand() {
        this.setName("plugins");
    }

    @Override
    public void execute(Loader server) {
        StringBuilder builder = new StringBuilder();
        builder.append("Plugins: ");
        for (PluginLoader.PluginEntry entry : server.getPluginLoader().getPlugins().values())
        {
            builder.append(String.format("%s v%s", entry.getName(), entry.getVersion())).append(" | ");
        }
        Logger.log(builder.toString());
    }


}

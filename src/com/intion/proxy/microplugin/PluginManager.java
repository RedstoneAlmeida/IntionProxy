package com.intion.proxy.microplugin;

import com.intion.proxy.Loader;
import com.intion.proxy.command.Command;
import com.intion.proxy.event.ProxyEvent;
import com.intion.proxy.task.IntionTask;
import com.intion.proxy.utils.Logger;

public class PluginManager {

    private Loader plugin;

    public PluginManager(Loader plugin)
    {
        this.plugin = plugin;
    }

    public void createCommand(String command, String function)
    {
        Command cmd = new Command() {
            @Override
            public String getName() {
                return command;
            }

            @Override
            public void execute(Loader server, String[] args) {
                server.getPluginLoader().call(function, (Object[]) args);
            }
        };
        this.plugin.getCommandMap().registerCommand(command, cmd);
    }

    public void createTask(String function, int delay, boolean repeat)
    {
        PluginTask task = new PluginTask(this.getPlugin().getScheduler(), function);
        this.plugin.getScheduler().addTask(task, delay, repeat);
    }

    public void callEvent(ProxyEvent event)
    {
        this.plugin.getPluginLoader().call(event.getEventName(), event);
    }

    public Loader getPlugin() {
        return plugin;
    }
}

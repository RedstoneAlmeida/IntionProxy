package com.intion.proxy.microplugin;

import com.intion.proxy.Loader;
import com.intion.proxy.command.Command;
import com.intion.proxy.event.ProxyEvent;
import com.intion.proxy.event.session.SessionInitializeEvent;
import com.intion.proxy.task.IntionTask;
import com.intion.proxy.utils.IntionConfig;
import com.intion.proxy.utils.Logger;

public class PluginManager {

    private Loader plugin;
    private PluginLoader.PluginEntry entry;

    public PluginManager(Loader plugin, PluginLoader.PluginEntry entry)
    {
        this.plugin = plugin;
        this.entry = entry;
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
                if (entry != null)
                    server.getPluginLoader().call(entry, function, (Object[]) args);
                else
                    server.getPluginLoader().call(function, (Object[]) args);
            }
        };
        this.plugin.getCommandMap().registerCommand(command, cmd);
    }

    public void createTask(String function, int delay, boolean repeat)
    {
        PluginTask task = new PluginTask(this.getPlugin().getScheduler(), function, this.entry);
        this.plugin.getScheduler().addTask(task, delay, repeat);
    }

    public IntionConfig createConfig(String filename)
    {
        return new IntionConfig(filename, true);
    }

    public void callEvent(ProxyEvent event)
    {
        this.plugin.getPluginLoader().call(event.getClass().getSimpleName(), event);
    }

    public void cantest(Object obj)
    {

    }

    public Loader getPlugin() {
        return plugin;
    }
}

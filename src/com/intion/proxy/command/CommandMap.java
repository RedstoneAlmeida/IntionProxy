package com.intion.proxy.command;

import com.intion.proxy.Loader;
import com.intion.proxy.utils.Logger;

import java.util.*;

public class CommandMap {

    private Loader plugin;

    private Map<String, Command> commandMap = new HashMap<>();
    private Map<String, Command> commands = new HashMap<>();

    public CommandMap(Loader plugin)
    {
        this.plugin = plugin;
    }

    public Map<String, Command> getCommandMap() {
        return commandMap;
    }

    public void registerCommand(String prefix, Command command)
    {
        this.commandMap.put(prefix, command);
        this.commands.put(command.getName().replace("/", ""), command);
    }

    public void execute(String line)
    {
        String[] split = line.split(" ");
        if (split.length < 1)
        {
            return;
        }
        String first = split[0].replace("/", "");
        if (commands.containsKey(first))
        {
            Command command = this.commands.get(first);
            List<String> list = Arrays.asList(split);

            Object a = this.plugin.getPluginLoader().call("eventCommand", command);

            if (a != null) {
                boolean value = (boolean) a;
                if (!value) {
                    return;
                }
            }
            if (split.length < 2)
            {
                command.execute(this.plugin);
                return;
            }
            command.execute(plugin, list.toArray(new String[split.length - 1]));
        }
    }

    public Map<String, Command> getCommands() {
        return commands;
    }

    public Loader getPlugin() {
        return plugin;
    }
}

package com.intion.proxy.microplugin;

import com.intion.proxy.task.IntionTask;
import com.intion.proxy.task.Scheduler;

public class PluginTask extends IntionTask {

    private String function;
    private PluginLoader.PluginEntry entry;

    public PluginTask(Scheduler scheduler, String function, PluginLoader.PluginEntry entry) {
        super(scheduler);
        this.function = function;
        this.entry = entry;
    }

    @Override
    public void onRun() {
        if (entry != null)
            this.getScheduler().getLoader().getPluginLoader().call(entry, this.function, this);
        else
            this.getScheduler().getLoader().getPluginLoader().call(this.function, this);
    }

}

package com.intion.proxy.microplugin;

import com.intion.proxy.task.IntionTask;
import com.intion.proxy.task.Scheduler;

public class PluginTask extends IntionTask {

    private String function;

    public PluginTask(Scheduler scheduler, String function) {
        super(scheduler);
        this.function = function;
    }

    @Override
    public void onRun() {
        this.getScheduler().getLoader().getPluginLoader().call(this.function, this);
    }

}

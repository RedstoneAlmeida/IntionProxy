package com.intion.proxy.task;

import com.intion.proxy.Loader;

public abstract class IntionTask {

    private Scheduler scheduler;
    private long taskId;

    /**
     * Delay, repeat
     *
     */
    private int delay = -1;
    private int maxDelay = -1;
    private boolean repeat = false;

    public IntionTask(Scheduler scheduler)
    {
        this.scheduler = scheduler;
        this.taskId = Scheduler.TaskID++;
    }

    public abstract void onRun();

    public long getTaskId() {
        return taskId;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void cancel()
    {
        this.scheduler.removeTask(this.taskId);
    }

    public void setMaxDelay(int maxDelay) {
        this.maxDelay = maxDelay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public int getMaxDelay() {
        return maxDelay;
    }

    public int getDelay() {
        return delay;
    }

    public boolean isRepeat() {
        return repeat;
    }
}

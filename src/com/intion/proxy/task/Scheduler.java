package com.intion.proxy.task;

import com.intion.proxy.Loader;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Scheduler {

    private Loader loader;

    public static long TaskID = -1;

    private Map<Long, IntionTask> queue = new ConcurrentHashMap<>();

    public Scheduler(Loader loader)
    {
        this.loader = loader;
    }

    public void init()
    {
        new Thread(() -> {
            while (true)
            {
                try {
                    Thread.sleep(1000);
                    this.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void run()
    {
        for (long taskId : queue.keySet())
        {
            IntionTask task = queue.get(taskId);
            int delay = task.getDelay();
            delay--;
            boolean repeat = task.isRepeat();
            if (repeat && delay <= 0)
            {
                task.onRun();
                task.setDelay(task.getMaxDelay());
                continue;
            } else if (!repeat && delay <= 0)
            {
                task.onRun();
                this.removeTask(taskId);
                continue;
            }
            task.setDelay(delay);
        }
    }

    public void addTask(IntionTask task)
    {
        this.addTask(task, 0);
    }

    public void addTask(IntionTask task, int delay)
    {
        this.addTask(task, delay, false);
    }

    public void addTask(IntionTask task, int delay, boolean repeat)
    {
        task.setDelay(delay);
        task.setMaxDelay(delay);
        task.setRepeat(repeat);
        this.queue.put(task.getTaskId(), task);
    }

    public void removeTask(long taskID)
    {
        this.queue.remove(taskID);
    }

    public Map<Long, IntionTask> getQueue() {
        return queue;
    }

    public Loader getLoader() {
        return loader;
    }
}

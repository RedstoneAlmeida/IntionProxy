package com.intion.proxy.command.defaults;

import com.intion.proxy.Loader;
import com.intion.proxy.Session;
import com.intion.proxy.command.Command;
import com.intion.proxy.utils.BungeeMath;
import com.intion.proxy.utils.Logger;

import java.util.concurrent.TimeUnit;

public class StatusCommand extends Command {

    private static final String UPTIME_FORMAT;

    public StatusCommand() {
        this.setName("status");
    }

    @Override
    public void execute(Loader server, String[] args) {
        Logger.log("---- " + "Proxy status" + " ----");

        long time = System.currentTimeMillis() - Loader.START_TIME;
        Logger.log("Uptime: " + formatUptime(time));

        Runtime runtime = Runtime.getRuntime();
        double totalMB = BungeeMath.round((double)runtime.totalMemory() / 1024.0D / 1024.0D, 2);
        double usedMB = BungeeMath.round((double)(runtime.totalMemory() - runtime.freeMemory()) / 1024.0D / 1024.0D, 2);
        double maxMB = BungeeMath.round((double)runtime.maxMemory() / 1024.0D / 1024.0D, 2);
        double usage = usedMB / maxMB * 100.0D;

        Logger.log("Thread count: " + Thread.getAllStackTraces().size());
        Logger.log("Used memory: " + usedMB + " MB. (" + BungeeMath.round(usage, 2) + "%)");
        Logger.log("Total memory: " + totalMB + " MB.");
        Logger.log("Maximum VM memory: "+ maxMB + " MB.");
        Logger.log("Available processors: " + runtime.availableProcessors());
        Logger.log("Connected Servers: " + server.getSessions().size());
        int players = 0;
        int maxPlayers = 0;
        for (Session session : server.getSessions().values())
        {
            players += session.getPlayerCount();
            maxPlayers += session.getMaxPlayers();
        }
        Logger.log("Players: " + players + "/" + maxPlayers);
    }

    private static String formatUptime(long uptime) {
        long days = TimeUnit.MILLISECONDS.toDays(uptime);
        uptime -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(uptime);
        uptime -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(uptime);
        uptime -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(uptime);
        return String.format(UPTIME_FORMAT, days, hours, minutes, seconds);
    }

    static {
        UPTIME_FORMAT = "%d" + " days " + "%d" + " hours " + "%d" + " minutes " + "%d" + " seconds";
    }
}

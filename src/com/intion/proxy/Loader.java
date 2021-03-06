package com.intion.proxy;

import com.intion.proxy.command.Command;
import com.intion.proxy.command.CommandMap;
import com.intion.proxy.command.defaults.*;
import com.intion.proxy.event.proxy.ProxyStartEvent;
import com.intion.proxy.event.session.SessionCreateEvent;
import com.intion.proxy.microplugin.PluginLoader;
import com.intion.proxy.network.Network;
import com.intion.proxy.network.protocol.CommandPacket;
import com.intion.proxy.network.protocol.DataPacket;
import com.intion.proxy.network.protocol.InformationPacket;
import com.intion.proxy.task.IntionTask;
import com.intion.proxy.task.Scheduler;
import com.intion.proxy.utils.IntionConfig;
import com.intion.proxy.utils.Logger;
import com.intion.proxy.utils.LoggerEnum;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Loader {

    public static final String PATH = System.getProperty("user.dir") + "/";
    public static final String DATA_PATH = System.getProperty("user.dir") + "/";
    public static final String PLUGIN_PATH = DATA_PATH + "plugins";
    public static final long START_TIME = System.currentTimeMillis();

    public static long SessionID = 0L;

    private Network network;
    private Scheduler scheduler;
    private PluginLoader pluginLoader;

    private CommandMap commandMap;

    private IntionConfig config;
    private IntionConfig banConfig;

    /**
     * Credentials
     */
    private String token = "Intion@123";

    /**
     * Connection
     */
    private short port;

    private final Map<Long, Session> sessions = new HashMap<>();

    public Loader(String[] args)
    {
        if (args.length > 0)
        {
            this.port = Short.parseShort(args[0]);
        } else {
            this.port = 25567;
        }
    }

    public void start()
    {
        Thread.currentThread().setName("Server Thread");
        LoggerEnum.INFO.log("Loading....");
        new File(PLUGIN_PATH).mkdir();
        try {
            this.config = new IntionConfig("proxy.properties");
            if (this.config.exist("server-password"))
                this.token = (String) this.config.get("server-password");
            else
            {
                this.config.put("server-password", "Intion@123");
                this.config.put("server-port", 25567);
                this.config.save();
                this.token = "Intion@123";
            }
            this.port = Short.parseShort((String) this.config.get("server-port"));
            this.banConfig = new IntionConfig("bans.properties");
            ServerSocket socket = new ServerSocket();
            socket.bind(new InetSocketAddress("0.0.0.0", this.port));
            this.network = new Network();
            this.scheduler = new Scheduler(this);
            this.scheduler.init();
            LoggerEnum.INFO.log("Starting Server...");
            LoggerEnum.INFO.log(String.format("Address: %s | port: %s", socket.getInetAddress().getHostAddress(), this.port));
            this.commandMap = new CommandMap(this);
            this.registerCommands();
            this.pluginLoader = new PluginLoader(this);
            this.pluginLoader.init();

            new Thread(() -> {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                    String line = "";
                    while (!line.equals("exit")) {
                        line = reader.readLine();
                        this.commandMap.execute(line);
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            ProxyStartEvent proxyStartEvent = new ProxyStartEvent(this);
            this.pluginLoader.getManager().callEvent(proxyStartEvent);

            this.scheduler.addTask(new IntionTask(this.scheduler) {
                @Override
                public void onRun() {
                    if (Loader.this.getSessions().isEmpty())
                        return;
                    for (long sessionId : Loader.this.getSessions().keySet())
                    {
                        Session session = Loader.this.getSessions().get(sessionId);
                        InformationPacket packet = new InformationPacket();
                        packet.information = "playerCount";
                        session.dataPacket(packet);
                    }
                }
            }, 5, true);

            while (true)
            {
                Socket sk = socket.accept();
                Session session = new Session(this, sk);
                long id = SessionID++;
                session.setServerId(id);

                SessionCreateEvent event = new SessionCreateEvent(session, id);
                this.pluginLoader.getManager().callEvent(event);

                this.sessions.put(id, session);
                session.start();
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public Session getSessionByName(String name)
    {
        for (Session session : this.sessions.values())
        {
            if (session.getSessionName().equalsIgnoreCase(name))
                return session;
        }
        return null;
    }

    public Session getSessionById(long id)
    {
        return this.sessions.getOrDefault(id, null);
    }

    public void dataPacket(DataPacket packet)
    {
        for (Session session : this.sessions.values())
        {
            session.dataPacket(packet);
        }
    }

    public void registerCommands()
    {
        this.commandMap.registerCommand("StopCommand", new StopCommand());
        this.commandMap.registerCommand("StatusCommand", new StatusCommand());
        this.commandMap.registerCommand("PingCommand", new PingCommand());
        this.commandMap.registerCommand("ListCommand", new ListCommand());
        this.commandMap.registerCommand("PluginsCommand", new PluginsCommand());
        this.commandMap.registerCommand("SayCommand", new SayCommand());
        this.commandMap.registerCommand("TransferCommand", new TransferCommand());
        this.commandMap.registerCommand("HelpCommand", new HelpCommand());
        this.commandMap.registerCommand("KickCommand", new KickCommand());
        this.commandMap.registerCommand("BanCommand", new BanCommand());
        this.commandMap.registerCommand("UnBanCommand", new UnbanCommand());
    }

    public Network getNetwork() {
        return network;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public CommandMap getCommandMap() {
        return commandMap;
    }

    public static void main(String[] args) {
        try {
            Loader loader = new Loader(args);
            loader.start();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void removeSession(long id)
    {
        this.sessions.remove(id);
    }

    public String getToken() {
        return token;
    }

    public Map<Long, Session> getSessions() {
        return sessions;
    }

    public PluginLoader getPluginLoader() {
        return pluginLoader;
    }

    public IntionConfig getBanConfig() {
        return banConfig;
    }
}

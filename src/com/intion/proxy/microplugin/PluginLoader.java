package com.intion.proxy.microplugin;

import com.intion.proxy.Loader;
import com.intion.proxy.utils.Logger;

import javax.script.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class PluginLoader {

    private Loader loader;
    private PluginManager manager;

    private ScriptEngine engine;
    private Map<String, PluginEntry> plugins = new HashMap<>();

    public PluginLoader(Loader loader)
    {
        this.loader = loader;

        final ScriptEngineManager manager = new ScriptEngineManager();
        this.engine = manager.getEngineByMimeType("text/javascript");
        this.manager = new PluginManager(loader, null);
    }

    public PluginManager getManager() {
        return manager;
    }

    public void init()
    {
        if (engine == null) {
            Logger.log("No JavaScript engine was found!");
            return;
        }

        if (!(engine instanceof Invocable)) {
            Logger.log("JavaScript engine does not support the Invocable API!");
            engine = null;
            return;
        }

        File dir = new File(Loader.PLUGIN_PATH);
        dir.mkdir();

        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if(file.isDirectory()) continue;
            if(file.getName().contains(".js")){
                try (final Reader reader = new InputStreamReader(new FileInputStream(file))) {
                    Reader fileReader = new InputStreamReader(new FileInputStream(file));
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    boolean bypassName = false;
                    boolean bypassVersion = false;
                    for (String line : bufferedReader.lines().collect(Collectors.toList()))
                    {
                        if (line.contains("getName()"))
                            bypassName = true;
                        if (line.contains("getVersion()"))
                            bypassVersion = true;

                        if (bypassName && bypassVersion) break;
                    }
                    if (!bypassName || !bypassVersion)
                    {
                        Logger.log("Could not load " + file.getName() + " not found name and version");
                        continue;
                    }
                    PluginEntry entry = new PluginEntry(fileReader, file.getName(), loader);
                    if (entry.isReady())
                        this.plugins.put(entry.getName(), entry);
                } catch (final Exception e) {
                    Logger.log("Could not load " + file.getName(), e);
                }
            }
        }
    }

    public synchronized Object call(String functionName, Object... args){
        for (PluginEntry entry : this.plugins.values())
        {
            entry.call(functionName, args);
        }
        return null;
    }

    public synchronized Object call(PluginEntry entry, String functionName, Object... args)
    {
        if (this.plugins.containsKey(entry.getName()))
            return entry.call(functionName, args);
        return null;
    }

    public Map<String, PluginEntry> getPlugins() {
        return plugins;
    }

    public Loader getLoader() {
        return loader;
    }

    public static class PluginEntry
    {
        private String name;
        private String version;
        private String filename;

        private PluginManager manager;

        private ScriptEngine engine;

        private boolean ready = false;

        public PluginEntry(Reader reader, String filename, Loader loader) {
            this.filename = filename;

            final ScriptEngineManager manager = new ScriptEngineManager();
            this.engine = manager.getEngineByMimeType("text/javascript");
            this.manager = new PluginManager(loader, this);

            engine.put("loader", loader);
            engine.put("scheduler", loader.getScheduler());
            engine.put("manager", this.manager);

            try {
                engine.eval(reader, engine.getBindings(ScriptContext.ENGINE_SCOPE));
            } catch (ScriptException e) {
                e.printStackTrace();
            }

            this.name = (String) this.call("getName");
            this.version = (String) this.call("getVersion");

            if (name == null || version == null)
            {
                return;
            }
            this.ready = true;

            Logger.log("Loaded plugin " + this.name);
            this.call("onEnable");
        }

        public boolean isReady() {
            return ready;
        }

        public synchronized Object call(String functionName, Object... args){
            if(engine.get(functionName) == null){
                return null;
            }
            try {
                return ((Invocable) engine).invokeFunction(functionName, args);
            } catch (final Exception se) {
                Logger.log("Error while calling " + functionName, se);
                se.printStackTrace();
            }
            return null;
        }

        public String getName() {
            return name;
        }

        public String getVersion() {
            return version;
        }

        public String getFilename() {
            return filename;
        }
    }
}

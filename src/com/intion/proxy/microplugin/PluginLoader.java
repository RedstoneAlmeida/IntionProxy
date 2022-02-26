package com.intion.proxy.microplugin;

import com.intion.proxy.Loader;
import com.intion.proxy.utils.Logger;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
        this.manager = new PluginManager(loader);
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

        engine.put("loader", this.loader);
        engine.put("scheduler", this.loader.getScheduler());
        engine.put("manager", this.manager);

        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if(file.isDirectory()) continue;
            if(file.getName().contains(".js")){
                try (final Reader reader = new InputStreamReader(new FileInputStream(file))) {
                    engine.eval(reader);
                    Logger.log("Loaded Script: " + file.getName());
                    this.checkPlugins(file.getName());
                } catch (final Exception e) {
                    Logger.log("Could not load " + file.getName(), e);
                }
            }
        }

        this.call("onEnable");
    }

    public void checkPlugins(String filename)
    {
        Object name = this.call("getName");
        if (name == null)
            return;
        Object version = this.call("getVersion");
        if (version == null)
            return;
        this.plugins.put(name.toString(), new PluginEntry(name.toString(), version.toString(), filename));
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

        public PluginEntry(String name, String version, String filename) {
            this.name = name;
            this.version = version;
            this.filename = filename;
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

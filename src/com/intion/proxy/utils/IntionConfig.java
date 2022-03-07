package com.intion.proxy.utils;

import com.intion.proxy.Loader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class IntionConfig {

    private Map<String, Object> map = new LinkedHashMap<>();
    private File file;

    public IntionConfig(String filename)
    {
        this.file = new File(Loader.DATA_PATH + "/" + filename);
        if (!this.file.exists())
        {
            try {
                if (!this.file.createNewFile())
                {
                    throw new IOException("not enabled");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            for (String line : bufferedReader.lines().collect(Collectors.toList()))
            {
                if (!line.contains("="))
                    continue;
                String[] split = line.split("=");
                this.map.put(split[0], split[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public boolean exist(String key)
    {
        return this.map.containsKey(key);
    }

    public void put(String key, Object object)
    {
        this.map.put(key, object);
    }

    public Object get(String key)
    {
        return this.map.getOrDefault(key, null);
    }

    public void save()
    {
        StringBuilder builder = new StringBuilder();
        for (String key : this.map.keySet())
        {
            builder.append(key).append("=").append(this.map.get(key).toString()).append("\n");
        }
        try {
            IntionConfig.writeFile(this.file, builder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void remove(String key)
    {
        this.map.remove(key);
    }

    public static void writeFile(String fileName, String content) throws IOException {
        writeFile((String)fileName, (InputStream)(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))));
    }

    public static void writeFile(String fileName, InputStream content) throws IOException {
        writeFile(new File(fileName), content);
    }

    public static void writeFile(File file, String content) throws IOException {
        writeFile((File)file, (InputStream)(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))));
    }

    public static void writeFile(File file, InputStream content) throws IOException {
        if (content == null) {
            throw new IllegalArgumentException("content must not be null");
        } else {
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream stream = new FileOutputStream(file);
            Throwable var3 = null;

            try {
                byte[] buffer = new byte[1024];

                int length;
                while((length = content.read(buffer)) != -1) {
                    stream.write(buffer, 0, length);
                }
            } catch (Throwable var13) {
                var3 = var13;
                throw var13;
            } finally {
                if (stream != null) {
                    if (var3 != null) {
                        try {
                            stream.close();
                        } catch (Throwable var12) {
                            var3.addSuppressed(var12);
                        }
                    } else {
                        stream.close();
                    }
                }

            }

            content.close();
        }
    }

}

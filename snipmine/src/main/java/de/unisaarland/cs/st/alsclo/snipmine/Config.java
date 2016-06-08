package de.unisaarland.cs.st.alsclo.snipmine;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;

public final class Config {

    private static final Logger L = LoggerFactory.getLogger(Config.class);

    private static final Config I = load(new File("snipmine.conf"));

    private int minStars = 5;
    private String snipcloudUser = "snipmine";
    private String snipcloudPw = "";
    private String snipcloudUrl = "https://snipcloud.alsclo.de/api";
    private String classpathFileName = "snipmine.classpath";
    private int concurrentConnections = 4;
    private int maxAvgWidth = 5;
    private int maxDepth = 15;

    private Config() {
    }

    public static int getMinStars() {
        return I.minStars;
    }

    public static String getSnipcloudUser() {
        return I.snipcloudUser;
    }

    public static String getSnipcloudPw() {
        return I.snipcloudPw;
    }

    public static String getSnipcloudUrl() {
        return I.snipcloudUrl;
    }

    public static String getClasspathFileName() {
        return I.classpathFileName;
    }

    public static int getConcurrentConnections() {
        return I.concurrentConnections;
    }

    public static int getMaxDepth() {
        return I.maxDepth;
    }

    public static int getMaxOrder(int depth) {
        return depth * I.maxAvgWidth;
    }

    private static Config load(File cfg) {
        if (!cfg.isFile() || !cfg.canRead()) {
            L.info("No config file found, using default.");
            return new Config();
        }
        try {
            Gson g = new Gson();
            JsonParser p = new JsonParser();
            return g.fromJson(p.parse(new FileReader(cfg)), Config.class);
        } catch (Exception e) {
            L.error("Error loading config, using default.", e);
            return new Config();
        }
    }
}


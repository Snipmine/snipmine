package org.snipcloud.server;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.util.logging.Logger;

/**
 * @author Alex Schlosser
 */
public final class Config {

    private static final Config I = load(new File("snipcloud.conf"));

    private String protocol = "http";
    private String host = "localhost";
    private int port = 8000;
    private String dbname = "snipcloud";
    private String dbuser = "snipcloud";
    private String dbpass = "";
    private int connectionCount = 4;

    private Config() {
    }

    public static String getProtocol() {
        return I.protocol;
    }

    public static String getHost() {
        return I.host;
    }

    public static int getPort() {
        return I.port;
    }

    public static String getDbname() {
        return I.dbname;
    }

    public static String getDbuser() {
        return I.dbuser;
    }

    public static String getDbpass() {
        return I.dbpass;
    }

    private static Config load(File cfg) {
        Logger l = Logger.getLogger(Config.class.getName());
        l.info("Loading config.");
        if (!cfg.isFile() || !cfg.canRead()) {
            l.info("No config file found, using default.");
            return new Config();
        }
        try {
            Gson g = new Gson();
            JsonParser p = new JsonParser();
            Config c = g.fromJson(p.parse(new FileReader(cfg)), Config.class);
            l.info("Port: " + c.port);
            return c;
        } catch (Exception e) {
            l.severe("Error loading config, using default.\n" + e);
            return new Config();
        }
    }

    public static int getConnectionCount() {
        return I.connectionCount;
    }
}

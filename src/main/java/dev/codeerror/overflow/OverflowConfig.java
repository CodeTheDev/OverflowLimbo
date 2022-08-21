package dev.codeerror.overflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

public class OverflowConfig {

    private final Properties config = new Properties();
    private final Logger logger = LoggerFactory.getLogger(OverflowConfig.class);

    private String serverBrand;
    private String serverAddress;
    private int serverPort;
    private int compressionThreshold;
    private int viewDistance;
    private int playerLimit;
    private String motd;
    private boolean velocityEnabled;
    private String velocitySecret;
    private boolean playerSkinsEnabled;

    public void saveDefaultConfig() {
        File defaultConfig = new File(new File(OverflowConfig.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile(), "server.properties");
        if (!defaultConfig.exists()) {
            try {
                Files.copy(Objects.requireNonNull(OverflowConfig.class.getResourceAsStream("/server.properties")), Paths.get(defaultConfig.getPath()));
                logger.info("Created default configuration.");
            } catch (Exception e) {
                logger.error("Unable to create default configuration.", e);
            }
        }
    }

    public void loadConfig() {
        try (FileReader in = new FileReader("server.properties")) {
            config.load(in);
            serverBrand = config.getProperty("server-brand", "OverflowLimbo");
            serverAddress = config.getProperty("server-address", "127.0.0.1");
            serverPort = Integer.parseInt(config.getProperty("server-port", "25565"));
            compressionThreshold = Integer.parseInt(config.getProperty("compression-threshold", "-1"));
            viewDistance = Integer.parseInt(config.getProperty("view-distance", "1"));
            playerLimit = Integer.parseInt(config.getProperty("max-players", "-1"));
            motd = config.getProperty("motd", "<dark_aqua><i>An OverflowLimbo Server</i></dark_aqua>");
            velocityEnabled = Boolean.parseBoolean(config.getProperty("velocity", "false"));
            velocitySecret = config.getProperty("velocity-secret", "");
            playerSkinsEnabled = Boolean.parseBoolean(config.getProperty("player-skins", "false"));
            logger.info("Loaded configuration.");
        } catch (IOException e) {
            logger.error("Unable to load/parse configuration.", e);
        }
    }

    public String getServerBrand() {
        return serverBrand;
    }
    public String getServerAddress() {
        return serverAddress;
    }
    public int getServerPort() {
        return serverPort;
    }
    public int getCompressionThreshold() {
        return compressionThreshold;
    }
    public int getViewDistance() {
        return viewDistance;
    }
    public int getPlayerLimit() {
        return playerLimit;
    }
    public String getMotd() {
        return motd;
    }
    public boolean isVelocityEnabled() {
        return velocityEnabled;
    }
    public String getVelocitySecret() {
        return velocitySecret;
    }
    public boolean isPlayerSkinsEnabled() {
        return playerSkinsEnabled;
    }

}

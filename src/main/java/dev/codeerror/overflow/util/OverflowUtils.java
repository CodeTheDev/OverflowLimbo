package dev.codeerror.overflow.util;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.util.Base64;

public class OverflowUtils {

    private static final Logger logger = LoggerFactory.getLogger(OverflowUtils.class);

    public static @NotNull String encodeServerIcon() {
        File icon = new File("server-icon.png");
        if (icon.exists()) {
            try {
                byte[] iconBytes = Files.readAllBytes(icon.toPath());
                return "data:image/png;base64," + Base64.getEncoder().encodeToString(iconBytes);
            } catch (Exception e) {
                logger.error("Unable to encode server-icon.png", e);
            }
        }
        return "";
    }

}

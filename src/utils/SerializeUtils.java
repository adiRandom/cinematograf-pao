package utils;

import java.io.File;

public class SerializeUtils {
    public static String getFilePath(String fileName) {
        // Get the app data folder based on platform
        String appDataDir;
        String osName = (System.getProperty("os.name")).toUpperCase();
        if (osName.contains("WIN")) {
            appDataDir = System.getenv("AppData");
            appDataDir += "\\.cinematograf-pao\\";
        } else {
            appDataDir = System.getProperty("user.home");
            if (osName.contains("MAC")) {
                appDataDir += "/Library/Application Support";
            }
            appDataDir += "/.cinematograf-pao/";
        }

        // Create the app data directory
        File theDir = new File(appDataDir);
        if (!theDir.exists()){
            theDir.mkdirs();
        }

        return appDataDir + fileName;
    }
}

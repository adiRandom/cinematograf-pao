package utils;

public class SerializeUtils {
    public static String getFilePath(String fileName) {
        // Get the app data folder based on platform
        String appDataDir;
        String osName = (System.getProperty("os.name")).toUpperCase();
        if (osName.contains("WIN")) {
            appDataDir = System.getenv("AppData");
        } else {
            appDataDir = System.getProperty("user.home");
            if (osName.contains("MAC")) {
                appDataDir += "/Library/Application Support";
            }
        }

        return appDataDir + "/.cinematograg-pao/" + fileName;
    }
}

package services;

import java.io.*;
import java.util.HashMap;

public class SerializationService {
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
        if (!theDir.exists()) {
            theDir.mkdirs();
        }

        return appDataDir + fileName;
    }

    public static void writeObject(Object obj, String fileName) {
        String filePath = SerializationService.getFilePath(fileName);

        try (FileOutputStream fileOutputStream
                     = new FileOutputStream(filePath);
             ObjectOutputStream objectOutputStream
                     = new ObjectOutputStream(fileOutputStream);) {

            objectOutputStream.writeObject(obj);
            objectOutputStream.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object readObject(String fileName) throws IOException, ClassNotFoundException {
        String filePath = SerializationService.getFilePath(fileName);
        FileInputStream fileInputStream
                = new FileInputStream(filePath);
        try (ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            Object obj = objectInputStream.readObject();
            return obj;
        }
    }
}

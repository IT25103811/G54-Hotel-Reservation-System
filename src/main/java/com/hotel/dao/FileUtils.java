package com.hotel.dao;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Utility class for file-based data storage.
 */
public class FileUtils {

    /**
     * Returns absolute path to the data/ directory.
     * Checks multiple locations to handle different deployment scenarios.
     */
    public static String getDataDirectory() {
        // Try relative to working directory (development / embedded tomcat)
        String workingDir = System.getProperty("user.dir");
        File dataDir = new File(workingDir, "data");
        if (dataDir.exists() && dataDir.isDirectory()) {
            return dataDir.getAbsolutePath();
        }

        // Try catalina.home (standalone Tomcat deployment)
        String catalinaHome = System.getProperty("catalina.home");
        if (catalinaHome != null) {
            dataDir = new File(catalinaHome, "data");
            if (dataDir.exists()) {
                return dataDir.getAbsolutePath();
            }
        }

        // Fall back to user.dir/data and create it
        dataDir = new File(workingDir, "data");
        dataDir.mkdirs();
        return dataDir.getAbsolutePath();
    }

    public static List<String> readLines(String filename) {
        List<String> lines = new ArrayList<>();
        File file = new File(getDataDirectory() + File.separator + filename);
        if (!file.exists()) {
            return lines;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file " + filename + ": " + e.getMessage());
        }
        return lines;
    }

    public static void writeLines(String filename, List<String> lines) {
        File dir = new File(getDataDirectory());
        dir.mkdirs();
        File file = new File(dir, filename);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing file " + filename + ": " + e.getMessage());
        }
    }

    public static String generateId(String prefix) {
        return prefix + System.currentTimeMillis();
    }

    public static void ensureFileExists(String filename) {
        File dir = new File(getDataDirectory());
        dir.mkdirs();
        File file = new File(dir, filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Error creating file " + filename + ": " + e.getMessage());
            }
        }
    }
}

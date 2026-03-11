package com.hotel;


import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Main {

    private static final int PORT = 8080;

    public static void main(String[] args) {
        try {
            // Suppress Digester warnings
            java.util.logging.Logger.getLogger("org.apache.tomcat.util.digester.Digester")
                    .setLevel(java.util.logging.Level.SEVERE);

            Tomcat tomcat = new Tomcat();
            tomcat.setPort(PORT);
            tomcat.getConnector();

            String webappDir = locateWebappDir();
            Context ctx = tomcat.addWebapp("", webappDir);
            ctx.setConfigFile(new File(webappDir, "WEB-INF/web.xml").toURI().toURL());

            tomcat.start();
            System.out.println("Grand Vista Hotel Reservation System started.");
            System.out.println("Open http://localhost:" + PORT + "/ in your browser.");
            tomcat.getServer().await();
        } catch (LifecycleException e) {
            System.err.println("Failed to start the server: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Failed to load webapp resources: " + e.getMessage());
            System.exit(1);
        } catch (URISyntaxException e) {
            System.err.println("Invalid resource path: " + e.getMessage());
            System.exit(1);
        }
    }

    private static String locateWebappDir() throws IOException, URISyntaxException {
        // IDE / mvn compile + exec:java — resources sit directly on disk
        File sourceWebapp = new File("src/main/webapp");
        if (sourceWebapp.isDirectory()) {
            return sourceWebapp.getAbsolutePath();
        }

        // Shaded-JAR run — webapp resources are packaged inside the JAR under "webapp/".
        // Tomcat requires a real file-system directory, so we extract to a temp location.
        URL jarUrl = Main.class.getProtectionDomain().getCodeSource().getLocation();
        Path tempDir = Files.createTempDirectory("hotel-webapp");
        tempDir.toFile().deleteOnExit();

        try (JarFile jar = new JarFile(Paths.get(jarUrl.toURI()).toFile())) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (!entry.getName().startsWith("webapp/")) {
                    continue;
                }
                Path target = tempDir.resolve(entry.getName().substring("webapp/".length()));
                if (entry.isDirectory()) {
                    Files.createDirectories(target);
                } else {
                    Files.createDirectories(target.getParent());
                    try (InputStream in = jar.getInputStream(entry)) {
                        Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
        }
        return tempDir.toString();
    }
}


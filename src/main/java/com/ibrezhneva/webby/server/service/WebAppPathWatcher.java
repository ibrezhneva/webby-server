package com.ibrezhneva.webby.server.service;

import lombok.Cleanup;
import lombok.SneakyThrows;

import java.io.File;
import java.nio.file.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class WebAppPathWatcher implements Runnable {
    private static final String DEFAULT_WEBAPPS_PATH = "webapps";
    private static final String WAR_EXTENSION = ".war";

    private AtomicBoolean isShutdown = new AtomicBoolean();
    private WebAppCreator webAppCreator;
    private String webappsPathString;
    private String warName;

    public WebAppPathWatcher(WebAppCreator webAppCreator) {
        this.webAppCreator = webAppCreator;
        this.webappsPathString = DEFAULT_WEBAPPS_PATH;
    }

    @SneakyThrows
    private void scan() {
        @Cleanup WatchService watchService = FileSystems.getDefault().newWatchService();
        Path webappsPath = Paths.get(new File(webappsPathString).getCanonicalPath());
        if(warName == null) {
            Files.walk(webappsPath)
                    .filter(e -> e.getFileName().toString().endsWith(WAR_EXTENSION))
                    .forEach(e -> webAppCreator.createWebApp(e.toString()));
        } else {
            webAppCreator.createWebApp(new File(webappsPathString, warName).getPath());
        }
        webappsPath.register(watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY);
        WatchKey key;
        while ((key = watchService.take()) != null && !isShutdown.get()) {
            for (WatchEvent<?> event : key.pollEvents()) {
                if (String.valueOf(event.context()).endsWith(WAR_EXTENSION)) {
                    String warPath = event.context().toString();
                    webAppCreator.createWebApp(warPath);
                }
            }
            key.reset();
        }
    }

    @Override
    public void run() {
        scan();
    }

    public void setShutdown(boolean isShutdown) {
        this.isShutdown.set(isShutdown);
    }

    public void setWebappsPathString(String webappsPathString) {
        if (webappsPathString != null) {
            this.webappsPathString = webappsPathString;
        } else {
            this.webappsPathString = DEFAULT_WEBAPPS_PATH;
        }
    }

    public void setWarName(String warName) {
        this.warName = warName;
    }
}

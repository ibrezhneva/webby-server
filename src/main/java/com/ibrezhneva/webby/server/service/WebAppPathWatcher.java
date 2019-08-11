package com.ibrezhneva.webby.server.service;

import lombok.SneakyThrows;

import java.io.File;
import java.nio.file.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class WebAppPathWatcher implements Runnable {
    private static final String WEBAPPS_FOLDER_NAME = "webapps";
    private static final String WAR_EXTENSION = ".war";

    private WebAppCreator webAppCreator;
    private AtomicBoolean isShutdown = new AtomicBoolean();

    public WebAppPathWatcher(WebAppCreator webAppCreator) {
        this.webAppCreator = webAppCreator;
    }

    @SneakyThrows
    private void scan() {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path webappsPath = Paths.get(new File(WEBAPPS_FOLDER_NAME).getCanonicalPath());

        Files.walk(webappsPath)
                .filter(e -> e.getFileName().toString().endsWith(WAR_EXTENSION))
                .forEach(e -> webAppCreator.createWebApp(e.toString()));

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
}

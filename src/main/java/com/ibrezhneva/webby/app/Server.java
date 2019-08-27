package com.ibrezhneva.webby.app;

import com.ibrezhneva.webby.entity.model.WebAppContainer;
import com.ibrezhneva.webby.service.RequestHandler;
import com.ibrezhneva.webby.service.WebAppCreator;
import com.ibrezhneva.webby.service.WebAppPathWatcher;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Server {
    private ServerConfig serverConfig;
    private boolean isShutdown;
    private ThreadPoolExecutor requestHandlerExecutor;
    private WebAppPathWatcher webAppPathWatcher;
    private WebAppContainer webAppContainer;

    public Server(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop, "Server-shutdown-thread"));
        webAppContainer = new WebAppContainer();
        webAppPathWatcher = new WebAppPathWatcher(new WebAppCreator(webAppContainer));
        webAppPathWatcher.setWebappsPathString(serverConfig.getPathToWebApps());
        webAppPathWatcher.setWarName(serverConfig.getWarName());
        new Thread(webAppPathWatcher).start();

        requestHandlerExecutor = new ThreadPoolExecutor(1,
                serverConfig.getMaxThreads(),
                serverConfig.getKeepAliveTimeout(),
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(serverConfig.getAcceptCount()));

        try (ServerSocket serverSocket = new ServerSocket(serverConfig.getPort())) {
            log.info("Server started. Port: {}", serverConfig.getPort());
            while (!isShutdown) {
                Socket socket = serverSocket.accept();
                RequestHandler requestHandler = new RequestHandler(socket, webAppContainer);
                requestHandlerExecutor.submit(requestHandler);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error during socket connection", e);
        }
    }

    private void stop() {
        isShutdown = true;
        webAppPathWatcher.setShutdown(true);
        requestHandlerExecutor.shutdown();
        webAppContainer.destroyWebApps();
        log.info("Server stopped");
    }
}

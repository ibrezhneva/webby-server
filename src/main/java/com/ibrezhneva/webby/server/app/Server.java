package com.ibrezhneva.webby.server.app;

import com.ibrezhneva.webby.server.entity.model.WebAppContainer;
import com.ibrezhneva.webby.server.service.RequestHandler;
import com.ibrezhneva.webby.server.service.WebAppCreator;
import com.ibrezhneva.webby.server.service.WebAppPathWatcher;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

@Slf4j
public class Server {
    private static final String CONFIGURATION_YAML = "configuration.yaml";
    private boolean isShutdown;
    private ThreadPoolExecutor requestHandlerExecutor;
    private WebAppPathWatcher webAppPathWatcher;
    private WebAppContainer webAppContainer;

    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop, "Server-shutdown-thread"));

        webAppContainer = new WebAppContainer();
        webAppPathWatcher = new WebAppPathWatcher(new WebAppCreator(webAppContainer));
        new Thread(webAppPathWatcher).start();
        ServerConfig serverConfig = getServerConfigFromYaml(CONFIGURATION_YAML);

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

    ServerConfig getServerConfigFromYaml(String configYamlFile) {
        try (InputStream inputStream = Server.class.getClassLoader().getResourceAsStream(configYamlFile)) {
            Yaml yaml = new Yaml(new Constructor(ServerConfig.class));
            return yaml.loadAs(inputStream, ServerConfig.class);
        } catch (IOException e) {
            throw new RuntimeException("Error during getting server configuration from yaml", e);
        }
    }
}

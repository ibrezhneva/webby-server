package com.ibrezhneva.webby.server.app;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStream;

public class Starter {
    private static final String CONFIGURATION_YAML = "configuration.yaml";

    public static void main(String[] args) {
        ServerConfig serverConfig = getServerConfigFromYaml(CONFIGURATION_YAML);
        Server server = new Server(serverConfig);
        server.start();
    }

    static ServerConfig getServerConfigFromYaml(String configYamlFile) {
        try (InputStream inputStream = Server.class.getClassLoader().getResourceAsStream(configYamlFile)) {
            Yaml yaml = new Yaml(new Constructor(ServerConfig.class));
            return yaml.loadAs(inputStream, ServerConfig.class);
        } catch (IOException e) {
            throw new RuntimeException("Error during getting server configuration from yaml", e);
        }
    }
}

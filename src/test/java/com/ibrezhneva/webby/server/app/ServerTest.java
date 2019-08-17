package com.ibrezhneva.webby.server.app;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ServerTest {
    private static final String CONFIGURATION_YAML = "configuration.yaml";

    @Test
    @DisplayName("Parse server config from yaml")
    void testGetServerConfigFromYaml() {
        Server server = new Server();
        ServerConfig serverConfig = server.getServerConfigFromYaml(CONFIGURATION_YAML);
        assertNotNull(serverConfig);
        assertEquals(serverConfig.getPort(), 8180);
        assertEquals(serverConfig.getMaxThreads(), 20);
        assertEquals(serverConfig.getKeepAliveTimeout(), 60);
        assertEquals(serverConfig.getAcceptCount(), 10);
    }
}
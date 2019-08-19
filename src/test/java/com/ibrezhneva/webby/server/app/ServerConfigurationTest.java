package com.ibrezhneva.webby.server.app;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ServerConfigurationTest {
    private static final String CONFIGURATION_YAML = "configuration.yaml";

    @Test
    @DisplayName("Parse server config from yaml")
    void testGetServerConfigFromYaml() {
        ServerConfig serverConfig = Starter.getServerConfigFromYaml(CONFIGURATION_YAML);
        assertNotNull(serverConfig);
        assertEquals(8180, serverConfig.getPort());
        assertEquals(20, serverConfig.getMaxThreads());
        assertEquals( 60, serverConfig.getKeepAliveTimeout());
        assertEquals(10, serverConfig.getAcceptCount());
        assertEquals("webapps", serverConfig.getPathToWebApps());
    }
}
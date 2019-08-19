package com.ibrezhneva.webby.server.integration;

import com.ibrezhneva.webby.server.app.Server;
import com.ibrezhneva.webby.server.app.ServerConfig;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetRequestITest {

    @Test
    public void testGetRequest() throws Exception {
        Runnable r = this::testServerStart;
        new Thread(r).start();

        Thread.sleep(1000);
        URL url = new URL("http://localhost:8180/test-only-get");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

        assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode());
        String headerField = connection.getHeaderField("Test-Header");
        assertEquals("test-header123", headerField);

        StringBuilder response = new StringBuilder();
        try (InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
             BufferedReader reader = new BufferedReader(streamReader)) {
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }
        }
        System.out.println(response.toString());
        assertEquals("Hello from GET!", response.toString());
    }

    private void testServerStart() {
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setPort(8180);
        serverConfig.setMaxThreads(20);
        serverConfig.setKeepAliveTimeout(60);
        serverConfig.setAcceptCount(10);
        serverConfig.setPathToWebApps("src/test/webapps");
        serverConfig.setWarName("test-only-get.war");
        Server server = new Server(serverConfig);
        server.start();
    }

}

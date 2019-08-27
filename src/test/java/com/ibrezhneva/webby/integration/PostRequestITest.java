package com.ibrezhneva.webby.integration;

import com.ibrezhneva.webby.app.Server;
import com.ibrezhneva.webby.app.ServerConfig;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PostRequestITest {

    @Test
    public void testPostRequest() throws Exception {
        Runnable r = this::testServerStart;
        new Thread(r).start();

        Thread.sleep(1000);
        URL url = new URL("http://localhost:8180/test-only-post/hello");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");

        connection.setDoOutput(true);
        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write("hello from test".getBytes());
            outputStream.flush();
        }
        assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode());

        StringBuilder response = new StringBuilder();
        try (InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
             BufferedReader reader = new BufferedReader(streamReader)) {
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }
        }
        assertEquals("Hello from POST!", response.toString());
    }

    private void testServerStart() {
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setPort(8180);
        serverConfig.setMaxThreads(20);
        serverConfig.setKeepAliveTimeout(60);
        serverConfig.setAcceptCount(10);
        serverConfig.setPathToWebApps("src/test/webapps");
        serverConfig.setWarName("test-only-post.war");
        Server server = new Server(serverConfig);
        server.start();
    }

}

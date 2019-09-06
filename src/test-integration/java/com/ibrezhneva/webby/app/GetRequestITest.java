package com.ibrezhneva.webby.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetRequestITest {
    private static final String URL_STRING = "http://localhost:8180/test-only-get/";
    private static final String EXPECTED_RESPONSE = "Hello from GET!";
    private static final String PATH_TO_WEB_APPS = "src/test-integration/webapps";
    private static final String WAR_NAME = "test-only-get.war";
    private static final String UNPACKED_WAR_NAME = "test-only-get";

    @Test
    public void testGetRequest() throws Exception {
        Runnable r = this::testServerStart;
        new Thread(r).start();

        Thread.sleep(1000);
        URL url = new URL(URL_STRING);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode());
        String testHeader = connection.getHeaderField("Test-Header");
        assertEquals("test-header123", testHeader);
        String contentLength = connection.getHeaderField("Content-Length");
        assertEquals(EXPECTED_RESPONSE.length(), Integer.parseInt(contentLength));

        StringBuilder response = new StringBuilder();
        try (InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
             BufferedReader reader = new BufferedReader(streamReader)) {
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }
        }
        assertEquals(EXPECTED_RESPONSE, response.toString());
    }

    private void testServerStart() {
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setPort(8180);
        serverConfig.setMaxThreads(20);
        serverConfig.setKeepAliveTimeout(60);
        serverConfig.setAcceptCount(10);
        serverConfig.setPathToWebApps(PATH_TO_WEB_APPS);
        serverConfig.setWarName(WAR_NAME);
        Server server = new Server(serverConfig);
        server.start();
    }

    @AfterEach
    void removeUnpackedWar() throws IOException {
        Files.walk(Paths.get(PATH_TO_WEB_APPS, UNPACKED_WAR_NAME))
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

}

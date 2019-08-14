package com.ibrezhneva.webby.server.service;

import com.ibrezhneva.webby.server.entity.model.WebApp;
import com.ibrezhneva.webby.server.entity.model.WebAppContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

class WebAppCreatorTest {
    private static final String WEBAPPS_DIR = "src/test/resources/webapps";
    private static final String WEBAPP_NAME = "test-web-app-1.0-SNAPSHOT";
    private static final String DEST_PATH = WEBAPPS_DIR + File.separator + WEBAPP_NAME;
    private static final String WAR_FILE_PATH = DEST_PATH + ".war";

    @BeforeEach
    void removeUnpackedWar() throws IOException {
        if (Files.notExists(Paths.get(DEST_PATH))) {
            return;
        }
        Files.walk(Paths.get(DEST_PATH))
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    @Test
    void testCreateWebApp() {
        WebAppContainer webAppContainer = new WebAppContainer();
        WebAppCreator webAppCreator = new WebAppCreator(webAppContainer);
        webAppCreator.createWebApp(WAR_FILE_PATH);
        WebApp webApp = webAppContainer.getWebApp(WEBAPP_NAME).get();
        assertNotNull(webApp);
        assertEquals(WEBAPP_NAME, webApp.getAppFolder());
        assertTrue(webApp.getServletPathToClassMap().containsKey("/"));
    }
}
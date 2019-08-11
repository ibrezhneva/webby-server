package com.ibrezhneva.webby.server.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class WarExtractorTest {
    private static final String WEBAPPS_DIR = "src/test/resources/webapps";
    private static final String WAR_FILE_PATH = WEBAPPS_DIR + "/test-web-app-1.0-SNAPSHOT.war";
    private static final String DEST_PATH = WEBAPPS_DIR + "/test-web-app-1.0-SNAPSHOT";

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
    void testExtractWar() {
        WarExtractor warExtractor = new WarExtractor();
        warExtractor.extractWar(WAR_FILE_PATH, DEST_PATH);
        assertTrue(Files.exists(Paths.get(DEST_PATH)));
    }
}
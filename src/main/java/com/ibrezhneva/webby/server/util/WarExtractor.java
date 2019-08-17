package com.ibrezhneva.webby.server.util;

import lombok.Cleanup;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class WarExtractor {
    private static final int BUFFER_SIZE = 8192;

    public static void extractWar(String path, String destPath) {
        File destDir = new File(destPath);
        byte[] buffer = new byte[BUFFER_SIZE];
        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(path));
             ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                File newFile = new File(destDir, zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    newFile.mkdirs();
                    zipEntry = zipInputStream.getNextEntry();
                    continue;
                }
                @Cleanup BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(newFile));
                int len;
                while ((len = zipInputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, len);
                }
                zipEntry = zipInputStream.getNextEntry();
            }
            zipInputStream.closeEntry();
        } catch (IOException e) {
            throw new RuntimeException("Error during war file extraction", e);
        }
    }
}

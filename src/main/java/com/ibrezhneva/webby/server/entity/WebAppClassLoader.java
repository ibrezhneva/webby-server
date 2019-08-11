package com.ibrezhneva.webby.server.entity;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class WebAppClassLoader extends ClassLoader {
    private static final String JAR_EXTENSION = ".jar";
    private static final String CLASS_EXTENSION = ".class";

    private List<String> paths;

    public WebAppClassLoader(Path webInfPath) {
        try {
            paths = Files.walk(webInfPath)
                    .filter(f -> f.toString().endsWith(JAR_EXTENSION))
                    .map(Path::toString)
                    .collect(Collectors.toList());
            paths.add(webInfPath.toString()+"/classes");
        } catch (IOException e) {
            throw new RuntimeException("Error during Class paths loading", e);
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String packagePathToClass = name.replace('.', '/').concat(CLASS_EXTENSION);
        try {
            for (String path : paths) {
                if (path.endsWith(JAR_EXTENSION)) {
                    JarFile jarFile = new JarFile(path);
                    Enumeration<JarEntry> entries = jarFile.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry jarEntry = entries.nextElement();

                        if (!jarEntry.isDirectory() && packagePathToClass.equals(jarEntry.getName())) {
                            try (InputStream inputStream = jarFile.getInputStream(jarEntry)) {
                                return getClass(name, inputStream);
                            }
                        }
                    }
                } else {
                    try (InputStream inputStream = new FileInputStream(new File(path, packagePathToClass))) {
                        return getClass(name, inputStream);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error during Class search", e);
        }
        return super.findClass(name);
    }

    private Class getClass(String name, InputStream inputStream) throws IOException {
        byte[] array = IOUtils.toByteArray(inputStream);
        Class clazz = defineClass(name, array, 0, array.length);
        resolveClass(clazz);
        return clazz;
    }
}

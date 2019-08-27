package com.ibrezhneva.webby.service;

import com.ibrezhneva.webby.entity.model.WebApp;
import com.ibrezhneva.webby.entity.model.WebAppContainer;
import com.ibrezhneva.webby.reader.DeploymentDescriptorHandler;
import com.ibrezhneva.webby.reader.entity.DeploymentDescriptor;
import com.ibrezhneva.webby.reader.entity.ServletDefinition;
import com.ibrezhneva.webby.reader.xml.XmlDeploymentDescriptorHandler;
import com.ibrezhneva.webby.util.WarExtractor;
import lombok.AllArgsConstructor;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
public class WebAppCreator {
    private static final String DEPLOYMENT_DESCRIPTOR_FILE_PATH = "/WEB-INF/web.xml";
    private static final String WAR_EXTENSION = ".war";
    private static final String JAR_EXTENSION = ".jar";
    private final DeploymentDescriptorHandler descriptorHandler = new XmlDeploymentDescriptorHandler();

    private WebAppContainer webAppContainer;

    public void createWebApp(String warPath) {
        String webAppPath = warPath.replace(WAR_EXTENSION, "");
        String webAppName = Paths.get(webAppPath).getFileName().toString();
        WarExtractor.extractWar(warPath, webAppPath);
        String deploymentDescriptorPath = webAppPath + DEPLOYMENT_DESCRIPTOR_FILE_PATH;

        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(deploymentDescriptorPath))) {
            DeploymentDescriptor descriptor = descriptorHandler.getDeploymentDescriptor(inputStream);
            WebApp webApp = initWebApp(webAppName, deploymentDescriptorPath, descriptor);
            webAppContainer.registerWebApp(webApp);
        } catch (Exception e) {
            throw new RuntimeException("Error during creation webApp by path: " + webAppPath, e);
        }
    }

    private WebApp initWebApp(String appName, String deploymentDescriptorPath, DeploymentDescriptor deploymentDescriptor) {
        WebApp webApp = new WebApp(appName);
        Path webInfPath = Paths.get(deploymentDescriptorPath).getParent();
        webApp.setClassLoader(getClassLoader(webInfPath));
        Map<String, Class<?>> servletPathToClassMap = deploymentDescriptor.getServletDefinitions()
                .stream()
                .collect(Collectors.toMap(ServletDefinition::getUrlPattern,
                        t -> getClass(webApp, t.getClassName())));

        webApp.setServletPathToClassMap(servletPathToClassMap);
        return webApp;
    }

    private URLClassLoader getClassLoader(Path webInfPath) {
        try {
            List<URL> paths = Files.walk(webInfPath)
                    .filter(f -> f.toString().endsWith(JAR_EXTENSION))
                    .map(this::getUrlFromPath)
                    .collect(Collectors.toList());
            paths.add(getUrlFromPath(Paths.get(webInfPath.toString(), "classes")));
            return new URLClassLoader(paths.toArray(new URL[0]));
        } catch (IOException e) {
            throw new RuntimeException("Error during Class paths loading", e);
        }
    }

    private URL getUrlFromPath(Path path) {
        try {
            return path.toUri().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error during getting URL from path  " + path, e);
        }
    }

    private Class<?> getClass(WebApp webApp, String className) {
        try {
            return webApp.getClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class not found for " + className, e);
        }
    }

}

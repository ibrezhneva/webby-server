package com.ibrezhneva.webby.server.service;

import com.ibrezhneva.webby.server.entity.model.WebApp;
import com.ibrezhneva.webby.server.entity.WebAppClassLoader;
import com.ibrezhneva.webby.server.entity.model.WebAppContainer;
import com.ibrezhneva.webby.server.reader.DeploymentDescriptorHandler;
import com.ibrezhneva.webby.server.reader.entity.DeploymentDescriptor;
import com.ibrezhneva.webby.server.reader.entity.ServletDefinition;
import com.ibrezhneva.webby.server.reader.xml.XmlDeploymentDescriptorHandler;
import com.ibrezhneva.webby.server.util.WarExtractor;
import lombok.AllArgsConstructor;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
public class WebAppCreator {
    private static final String DEPLOYMENT_DESCRIPTOR_FILE_PATH = "/WEB-INF/web.xml";
    private static final String WAR_EXTENSION = ".war";

    private WebAppContainer webAppContainer;

    public void createWebApp(String warPath) {
        WarExtractor warExtractor = new WarExtractor();
        DeploymentDescriptorHandler descriptorHandler = new XmlDeploymentDescriptorHandler();

        String webAppPath = warPath.replace(WAR_EXTENSION, "");
        String webAppName = Paths.get(webAppPath).getFileName().toString();
        warExtractor.extractWar(warPath, webAppPath);
        String deploymentDescriptorPath = webAppPath + DEPLOYMENT_DESCRIPTOR_FILE_PATH;
        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(deploymentDescriptorPath))) {
            DeploymentDescriptor descriptor = descriptorHandler.getDeploymentDescriptor(inputStream, deploymentDescriptorPath);
            WebApp webApp = initWebApp(webAppName, deploymentDescriptorPath, descriptor);
            webAppContainer.registerWebApp(webApp);
        } catch (IOException e) {
            throw new RuntimeException("Error during creation webApp by path: " + webAppPath, e);
        }
    }

    private WebApp initWebApp(String appName, String deploymentDescriptorPath, DeploymentDescriptor deploymentDescriptor) {
        WebApp webApp = new WebApp();
        webApp.setAppFolder(appName);
        Path webInfPath = Paths.get(deploymentDescriptorPath).getParent();
        webApp.setClassLoader(new WebAppClassLoader(webInfPath));
        Map<String, Class<?>> servletPathToClassMap = deploymentDescriptor.getServletDefinitions()
                .stream()
                .collect(Collectors.toMap(ServletDefinition::getUrlPattern,
                        t -> {
                            try {
                                return webApp.getClassLoader().loadClass(t.getClassName());
                            } catch (ClassNotFoundException e) {
                                throw new RuntimeException("Class not found for " + t.getClassName(), e);
                            }
                        }));

        webApp.setServletPathToClassMap(servletPathToClassMap);
        return webApp;
    }
}

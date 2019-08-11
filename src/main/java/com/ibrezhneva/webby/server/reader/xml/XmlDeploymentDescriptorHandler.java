package com.ibrezhneva.webby.server.reader.xml;

import com.ibrezhneva.webby.server.reader.DeploymentDescriptorHandler;
import com.ibrezhneva.webby.server.reader.entity.DeploymentDescriptor;
import com.ibrezhneva.webby.server.reader.entity.ServletDefinition;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class XmlDeploymentDescriptorHandler implements DeploymentDescriptorHandler {
    private static final String SERVLET_TAG = "servlet";
    private static final String SERVLET_NAME_TAG = "servlet-name";
    private static final String SERVLET_CLASS_TAG = "servlet-class";
    private static final String SERVLET_MAPPING_TAG = "servlet-mapping";
    private static final String URL_PATTERN_TAG = "url-pattern";

    @Override
    public DeploymentDescriptor getDeploymentDescriptor(InputStream inputStream, String path) {
        DeploymentDescriptor deploymentDescriptor = new DeploymentDescriptor();
        Map<String, ServletDefinition> servletDefinitionMap = new HashMap<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            NodeList servletNodes = document.getDocumentElement().getElementsByTagName(SERVLET_TAG);
            for (int i = 0; i < servletNodes.getLength(); i++) {
                Node servletNode = servletNodes.item(i);
                Element element = (Element) servletNode;
                ServletDefinition servletDefinition = new ServletDefinition();
                servletDefinition.setName(element.getElementsByTagName(SERVLET_NAME_TAG).item(0).getTextContent());
                servletDefinition.setClassName(element.getElementsByTagName(SERVLET_CLASS_TAG).item(0).getTextContent());
                servletDefinitionMap.put(servletDefinition.getName(), servletDefinition);
            }
            NodeList servletMappingNodes = document.getDocumentElement().getElementsByTagName(SERVLET_MAPPING_TAG);
            for (int i = 0; i < servletMappingNodes.getLength(); i++) {
                Node servletMappingNode = servletMappingNodes.item(i);
                Element element = (Element) servletMappingNode;
                ServletDefinition servletDefinition = servletDefinitionMap.get(element.getElementsByTagName(SERVLET_NAME_TAG).item(0).getTextContent());
                servletDefinition.setUrlPattern(element.getElementsByTagName(URL_PATTERN_TAG).item(0).getTextContent());
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException("Error during parsing file by path: " + path, e);
        }

        deploymentDescriptor.setServletDefinitions(new ArrayList<>(servletDefinitionMap.values()));
        return deploymentDescriptor;
    }
}

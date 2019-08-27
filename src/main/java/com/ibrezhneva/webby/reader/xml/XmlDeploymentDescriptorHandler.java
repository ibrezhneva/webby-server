package com.ibrezhneva.webby.reader.xml;

import com.ibrezhneva.webby.reader.DeploymentDescriptorHandler;
import com.ibrezhneva.webby.reader.entity.DeploymentDescriptor;
import com.ibrezhneva.webby.reader.entity.ServletDefinition;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlDeploymentDescriptorHandler implements DeploymentDescriptorHandler {

    private static final DocumentBuilderFactory BUILDER_FACTORY = DocumentBuilderFactory.newInstance();
    private static final String SERVLET_TAG = "servlet";
    private static final String SERVLET_NAME_TAG = "servlet-name";
    private static final String SERVLET_CLASS_TAG = "servlet-class";
    private static final String SERVLET_MAPPING_TAG = "servlet-mapping";
    private static final String URL_PATTERN_TAG = "url-pattern";

    @Override
    public DeploymentDescriptor getDeploymentDescriptor(InputStream inputStream) {
        DeploymentDescriptor deploymentDescriptor = new DeploymentDescriptor();
        Map<String, String> servletNameToClassMap = new HashMap<>();
        List<ServletDefinition> servletDefinitions = new ArrayList<>();

        try {
            DocumentBuilder builder = BUILDER_FACTORY.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            NodeList servletNodes = document.getDocumentElement().getElementsByTagName(SERVLET_TAG);
            for (int i = 0; i < servletNodes.getLength(); i++) {
                Node servletNode = servletNodes.item(i);
                Element element = (Element) servletNode;
                String servletName = element.getElementsByTagName(SERVLET_NAME_TAG).item(0).getTextContent();
                String className = element.getElementsByTagName(SERVLET_CLASS_TAG).item(0).getTextContent();
                servletNameToClassMap.put(servletName, className);
            }
            NodeList servletMappingNodes = document.getDocumentElement().getElementsByTagName(SERVLET_MAPPING_TAG);
            for (int i = 0; i < servletMappingNodes.getLength(); i++) {
                Node servletMappingNode = servletMappingNodes.item(i);
                Element element = (Element) servletMappingNode;
                String servletName = element.getElementsByTagName(SERVLET_NAME_TAG).item(0).getTextContent();
                String className = servletNameToClassMap.get(servletName);

                for (int j = 0; j < element.getElementsByTagName(URL_PATTERN_TAG).getLength(); j++) {
                    String urlPattern = element.getElementsByTagName(URL_PATTERN_TAG).item(j).getTextContent();
                    ServletDefinition servletDefinition = new ServletDefinition(className, urlPattern);
                    servletDefinitions.add(servletDefinition);
                }
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException("Error during parsing file ", e);
        }

        deploymentDescriptor.setServletDefinitions(servletDefinitions);
        return deploymentDescriptor;
    }
}

package com.ibrezhneva.webby.server.reader.xml;

import com.ibrezhneva.webby.server.reader.DeploymentDescriptorHandler;
import com.ibrezhneva.webby.server.reader.entity.DeploymentDescriptor;
import com.ibrezhneva.webby.server.reader.entity.ServletDefinition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class XmlDeploymentDescriptorHandlerTest {

    private static final String DEPLOYMENT_DESCRIPTOR_PATH = "web.xml";

    @Test
    @DisplayName("Parse deployment descriptor xml")
    void testGetDeploymentDescriptor() {
        List<ServletDefinition> servletDefinitions = new ArrayList<>();
        ServletDefinition servletDefinition0 = new ServletDefinition();
        servletDefinition0.setClassName("com.ibrezhneva.webby.server.entity.servlet.GetAllProductServlet");
        servletDefinition0.setUrlPattern("/products");
        servletDefinitions.add(servletDefinition0);

        ServletDefinition servletDefinition1 = new ServletDefinition();
        servletDefinition1.setClassName("com.ibrezhneva.webby.server.entity.servlet.GetAllProductServlet");
        servletDefinition1.setUrlPattern("/");
        servletDefinitions.add(servletDefinition1);

        ServletDefinition servletDefinition2 = new ServletDefinition();
        servletDefinition2.setClassName("com.ibrezhneva.webby.server.entity.servlet.AddProductServlet");
        servletDefinition2.setUrlPattern("/product/add");
        servletDefinitions.add(servletDefinition2);

        ServletDefinition servletDefinition3 = new ServletDefinition();
        servletDefinition3.setClassName("com.ibrezhneva.webby.server.entity.servlet.UpdateProductServlet");
        servletDefinition3.setUrlPattern("/product/edit");
        servletDefinitions.add(servletDefinition3);

        DeploymentDescriptorHandler handler = new XmlDeploymentDescriptorHandler();
        InputStream in = XmlDeploymentDescriptorHandlerTest.class.getClassLoader().getResourceAsStream(DEPLOYMENT_DESCRIPTOR_PATH);
        DeploymentDescriptor deploymentDescriptor = handler.getDeploymentDescriptor(in);
        assertNotNull(deploymentDescriptor);
        assertTrue(deploymentDescriptor.getServletDefinitions().containsAll(servletDefinitions));
        assertTrue(servletDefinitions.containsAll(deploymentDescriptor.getServletDefinitions()));
    }
}
package com.ibrezhneva.webby.reader.xml;

import com.ibrezhneva.webby.reader.DeploymentDescriptorHandler;
import com.ibrezhneva.webby.reader.entity.DeploymentDescriptor;
import com.ibrezhneva.webby.reader.entity.FilterDefinition;
import com.ibrezhneva.webby.reader.entity.ServletDefinition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class XmlDeploymentDescriptorHandlerTest {

    private static final String DEPLOYMENT_DESCRIPTOR_PATH = "web.xml";

    @Test
    @DisplayName("Parse deployment descriptor xml")
    void testGetDeploymentDescriptor() {
        List<ServletDefinition> servletDefinitions = new ArrayList<>();
        ServletDefinition servletDefinition0 = new ServletDefinition();
        servletDefinition0.setClassName("com.ibrezhneva.webby.entity.servlet.GetAllProductServlet");
        servletDefinition0.setUrlPattern("/products");
        servletDefinitions.add(servletDefinition0);

        ServletDefinition servletDefinition1 = new ServletDefinition();
        servletDefinition1.setClassName("com.ibrezhneva.webby.entity.servlet.GetAllProductServlet");
        servletDefinition1.setUrlPattern("/");
        servletDefinitions.add(servletDefinition1);

        ServletDefinition servletDefinition2 = new ServletDefinition();
        servletDefinition2.setClassName("com.ibrezhneva.webby.entity.servlet.AddProductServlet");
        servletDefinition2.setUrlPattern("/product/add");
        servletDefinitions.add(servletDefinition2);

        ServletDefinition servletDefinition3 = new ServletDefinition();
        servletDefinition3.setClassName("com.ibrezhneva.webby.entity.servlet.UpdateProductServlet");
        servletDefinition3.setUrlPattern("/product/edit");
        servletDefinitions.add(servletDefinition3);

        FilterDefinition filterDefinition = new FilterDefinition();
        filterDefinition.setClassName("com.ibrezhneva.webby.entity.filter.AuthorizationFilter");
        filterDefinition.setUrlPattern("/product/*");

        DeploymentDescriptorHandler handler = new XmlDeploymentDescriptorHandler();
        InputStream in = XmlDeploymentDescriptorHandlerTest.class.getClassLoader().getResourceAsStream(DEPLOYMENT_DESCRIPTOR_PATH);
        DeploymentDescriptor deploymentDescriptor = handler.getDeploymentDescriptor(in);
        assertNotNull(deploymentDescriptor);
        List<ServletDefinition> actualServletDefinitions = deploymentDescriptor.getServletDefinitions();
        assertTrue(actualServletDefinitions.containsAll(servletDefinitions));
        assertTrue(servletDefinitions.containsAll(actualServletDefinitions));

        List<FilterDefinition> actualFilterDefinitions = deploymentDescriptor.getFilterDefinitions();
        assertNotNull(actualFilterDefinitions);
        assertEquals(1, actualFilterDefinitions.size());
        assertTrue(actualFilterDefinitions.contains(filterDefinition));
    }
}
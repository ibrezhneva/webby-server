package com.ibrezhneva.webby.server.reader.entity;

import lombok.Data;

import java.util.List;

@Data
public class DeploymentDescriptor {
    private List<ServletDefinition> servletDefinitions;
}

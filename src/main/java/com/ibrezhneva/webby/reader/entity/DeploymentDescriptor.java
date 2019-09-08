package com.ibrezhneva.webby.reader.entity;

import lombok.Data;

import java.util.List;

@Data
public class DeploymentDescriptor {
    private List<ServletDefinition> servletDefinitions;
    private List<FilterDefinition> filterDefinitions;
}

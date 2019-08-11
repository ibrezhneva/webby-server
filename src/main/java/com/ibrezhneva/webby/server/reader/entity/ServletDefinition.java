package com.ibrezhneva.webby.server.reader.entity;

import lombok.Data;

@Data
public class ServletDefinition {
    private String name;
    private String className;
    private String urlPattern;
}

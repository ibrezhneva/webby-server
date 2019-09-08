package com.ibrezhneva.webby.reader.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterDefinition {
    private String className;
    private String urlPattern;
}

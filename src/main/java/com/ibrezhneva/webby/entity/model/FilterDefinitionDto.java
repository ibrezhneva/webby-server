package com.ibrezhneva.webby.entity.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.servlet.Filter;

@Data
@AllArgsConstructor
public class FilterDefinitionDto {
    private String urlPattern;
    private Class<? extends Filter> clazz;
}

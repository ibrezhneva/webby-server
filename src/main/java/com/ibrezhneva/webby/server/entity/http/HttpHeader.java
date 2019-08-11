package com.ibrezhneva.webby.server.entity.http;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HttpHeader {
    private String name;
    private String value;
}

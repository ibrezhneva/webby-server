package com.ibrezhneva.webby.entity.http;

import lombok.Getter;

@Getter
public enum HttpHeaderName {
    CONTENT_LENGTH("Content-Length"),
    TRANSFER_ENCODING("Transfer-Encoding"),
    CONTENT_TYPE("Content-Type"),
    COOKIE("Cookie"),
    LOCATION("Location");

    private String name;

    HttpHeaderName(String name) {
        this.name = name;
    }
}

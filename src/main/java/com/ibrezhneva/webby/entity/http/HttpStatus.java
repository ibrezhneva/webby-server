package com.ibrezhneva.webby.entity.http;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum HttpStatus {

    OK(200, "OK"),
    FOUND(302, "Found"),
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private final int statusCode;
    private final String statusMessage;

    HttpStatus(int statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public static HttpStatus getByStatusCode(int code) {
        return Arrays.stream(values())
                .filter(e -> e.statusCode == code)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("No HttpStatus for code: " + code + " is found"));
    }

    @Override
    public String toString() {
        return statusCode + " " + statusMessage;
    }
}

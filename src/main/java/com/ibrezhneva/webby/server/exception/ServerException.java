package com.ibrezhneva.webby.server.exception;

import com.ibrezhneva.webby.server.entity.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ServerException extends RuntimeException {
    private HttpStatus httpStatus;
    private String message;
}
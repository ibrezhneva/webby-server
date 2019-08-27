package com.ibrezhneva.webby.exception;

import com.ibrezhneva.webby.entity.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ServerException extends RuntimeException {
    private HttpStatus httpStatus;
    private String message;
}
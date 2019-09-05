package com.ibrezhneva.web.app.service.impl;

import com.ibrezhneva.web.app.service.MessageService;

public class DefaultMessageService implements MessageService {

    @Override
    public String addGreeting(String greeting) {
        if (greeting.contains("hello")) {
            return "Hello from POST!";
        }
        throw new RuntimeException("No greeting!");
    }
}

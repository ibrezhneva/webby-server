package com.ibrezhneva.web.app.service.impl;

import com.ibrezhneva.web.app.service.MessageService;

public class DefaultMessageService implements MessageService {

    @Override
    public String getMessage() {
        return "Hello from GET!";
    }

}

package com.ibrezhneva.web.app;

import com.ibrezhneva.web.app.service.impl.DefaultMessageService;

import java.util.HashMap;
import java.util.Map;

public class ServiceLocator {
    private static final Map<Class<?>, Object> SERVICES = new HashMap<>();

    static {
        SERVICES.put(DefaultMessageService.class, new DefaultMessageService());
    }

    public static <T> T getService(Class<T> clazz) {
        return clazz.cast(SERVICES.get(clazz));
    }
}

package com.ibrezhneva.webby.server.entity.model;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class WebAppContainer {
    private Map<String, WebApp> webAppMap = new ConcurrentHashMap<>();

    public void registerWebApp(WebApp webApp) {
        webAppMap.put(webApp.getAppFolder(), webApp);
    }

    public Optional<WebApp> getWebApp(String webAppName) {
        return Optional.ofNullable(webAppMap.get(webAppName));
    }

    public void decommissionWebApps() {
        webAppMap.values().forEach(WebApp::destroy);
    }
}

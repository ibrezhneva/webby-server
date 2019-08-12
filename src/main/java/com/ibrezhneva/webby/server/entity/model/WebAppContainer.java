package com.ibrezhneva.webby.server.entity.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebAppContainer {
    private Map<String, WebApp> webAppMap = new ConcurrentHashMap<>();

    public void registerWebApp(WebApp webApp) {
        webAppMap.put(webApp.getAppFolder(), webApp);
    }

    public WebApp getWebApp(String webAppName) {
        return webAppMap.get(webAppName);
    }

    public void decommissionWebApps() {
        webAppMap.values().forEach(WebApp::destroy);
    }
}

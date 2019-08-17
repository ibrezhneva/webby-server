package com.ibrezhneva.webby.server.app;

import lombok.Data;

@Data
public class ServerConfig {
    private int port;
    private int maxThreads;
    private int keepAliveTimeout;
    private int acceptCount;
}

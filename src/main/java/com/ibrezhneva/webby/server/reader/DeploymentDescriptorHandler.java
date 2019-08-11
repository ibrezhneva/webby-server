package com.ibrezhneva.webby.server.reader;

import com.ibrezhneva.webby.server.reader.entity.DeploymentDescriptor;

import java.io.InputStream;

public interface DeploymentDescriptorHandler {
    DeploymentDescriptor getDeploymentDescriptor(InputStream inputStream, String path);
}

package com.ibrezhneva.webby.reader;

import com.ibrezhneva.webby.reader.entity.DeploymentDescriptor;

import java.io.InputStream;

public interface DeploymentDescriptorHandler {
    DeploymentDescriptor getDeploymentDescriptor(InputStream inputStream);
}

package com.ibrezhneva.webby.entity.model;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AppServletInputStream extends ServletInputStream {

    private InputStream inputStream;

    public AppServletInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public int read() throws IOException {
        return inputStream.read();
    }

    @Override
    public boolean isFinished() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public boolean isReady() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public void setReadListener(ReadListener readListener) {
        throw new UnsupportedOperationException("Method is not supported");
    }
}

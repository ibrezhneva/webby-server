package com.ibrezhneva.webby.server.entity.model;

import lombok.Setter;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.IOException;
import java.io.OutputStream;

public class AppServletOutputStream extends ServletOutputStream {
    private static final int BUFFER_SIZE = 1024;

    private byte[] buffer = new byte[BUFFER_SIZE];
    private OutputStream outputStream;
    private int index;
    @Setter
    private String responseStatusLine;
    @Setter
    private String headers;
    private boolean isStatusLineWritten;

    public AppServletOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public boolean isReady() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public void write(int b) throws IOException {
        if (index == buffer.length) {
            flush();
        }
        buffer[index] = (byte) b;
        index++;
    }

    @Override
    public void flush() throws IOException {
        if(!isStatusLineWritten) {
            outputStream.write(responseStatusLine.getBytes());
            outputStream.write(headers.getBytes());
            isStatusLineWritten = true;
        }
        outputStream.write(buffer, 0, index);
        index = 0;
    }
}


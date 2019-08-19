package com.ibrezhneva.webby.server.entity.model;

import java.io.IOException;
import java.io.InputStream;

public class RequestInputStream extends InputStream {
    private static final int DEFAULT_BUFFER_SIZE = 256;
    private static final byte[] LINE_BUFFER = new byte[DEFAULT_BUFFER_SIZE];
    private static final int CR = '\r';
    private static final int LF = '\n';

    private int prevByte = -1;
    private InputStream inputStream;

    public RequestInputStream(InputStream inputStream) {
        this.inputStream = inputStream;

    }

    @Override
    public int read() throws IOException {
        int b = inputStream.read();
        if (b == LF) {
            if (prevByte == CR) {
                return -1;
            }
        }
        prevByte = b;
        return b;
    }

    public String readLine() throws IOException {
        StringBuilder line = new StringBuilder();
        while (true) {
            for (int i = 0; i < LINE_BUFFER.length; i++) {
                int n = read();
                if (n == -1) {
                    byte[] remainder = new byte[i - 1]; //exclude line-termination characters
                    System.arraycopy(LINE_BUFFER, 0, remainder, 0, remainder.length);
                    line.append(new String(remainder));
                    return line.toString();
                }
                LINE_BUFFER[i] = (byte) n;
            }
            line.append(new String(LINE_BUFFER));
        }
    }
}

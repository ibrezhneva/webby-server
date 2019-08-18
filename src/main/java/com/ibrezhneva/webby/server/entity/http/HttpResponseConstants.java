package com.ibrezhneva.webby.server.entity.http;

public abstract class HttpResponseConstants {
    public static final String CRLF = "\r\n";
    public static final byte[] CRLF_BYTES = CRLF.getBytes();
    public static final String HTTP_VERSION = "HTTP/1.1";
}

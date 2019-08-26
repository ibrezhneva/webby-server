package com.ibrezhneva.webby.server.entity.model;

import com.ibrezhneva.webby.server.entity.adapter.HttpServletResponseAdapter;
import com.ibrezhneva.webby.server.entity.http.HttpHeader;
import com.ibrezhneva.webby.server.entity.http.HttpHeaderName;
import com.ibrezhneva.webby.server.entity.http.HttpResponseConstants;
import com.ibrezhneva.webby.server.entity.http.HttpStatus;
import lombok.Setter;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@Setter
public class AppServletResponse extends HttpServletResponseAdapter {
    private AppServletOutputStream servletOutputStream;
    private PrintWriter writer;
    private String contentType;
    private List<HttpHeader> headers = new ArrayList<>();
    private List<Cookie> cookies = new ArrayList<>();
    private HttpStatus httpStatus;
    private int status;

    public AppServletResponse(OutputStream outputStream) {
        this.servletOutputStream = new AppServletOutputStream(outputStream);
        OutputStreamWriter streamWriter = new OutputStreamWriter(servletOutputStream);
        this.writer = new PrintWriter(streamWriter, true);
    }

    @Override
    public void addCookie(Cookie cookie) {
        cookies.add(cookie);
    }

    @Override
    public void sendRedirect(String location) {
        setStatus(HttpStatus.MOVED_PERMANENTLY.getStatusCode());
        addHeader(HttpHeaderName.LOCATION.getName(), location);
    }

    @Override
    public void setHeader(String name, String value) {
        headers.removeIf(e -> e.getName().equals(name));
        addHeader(name, value);
    }

    @Override
    public void addHeader(String name, String value) {
        headers.add(new HttpHeader(name, value));
    }

    @Override
    public void setStatus(int sc) {
        status = sc;
        httpStatus = HttpStatus.getByStatusCode(sc);
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public ServletOutputStream getOutputStream() {
        return servletOutputStream;
    }

    @Override
    public PrintWriter getWriter() {
        if (writer == null) {
            writer = new PrintWriter(servletOutputStream, true);
        }
        return writer;
    }

    @Override
    public void setContentType(String type) {
        setHeader(HttpHeaderName.CONTENT_TYPE.getName(), type);
    }

    class AppServletOutputStream extends ServletOutputStream {
        private static final int BUFFER_SIZE = 1024;

        private byte[] buffer = new byte[BUFFER_SIZE];
        private OutputStream outputStream;
        private int index;
        private boolean isStatusLineWritten;

        AppServletOutputStream(OutputStream outputStream) {
            this.outputStream = outputStream;
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
            if (!isStatusLineWritten) {
                outputStream.write(getResponseStatusLine());
                outputStream.write(getResponseHeaders());
                isStatusLineWritten = true;
            }
            outputStream.write(buffer, 0, index);
            index = 0;
        }

        @Override
        public boolean isReady() {
            throw new UnsupportedOperationException("Method is not supported");
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
            throw new UnsupportedOperationException("Method is not supported");
        }
    }

    private byte[] getResponseStatusLine() {
        String responseStatusLine = HttpResponseConstants.HTTP_VERSION + " " +
                httpStatus +
                HttpResponseConstants.CRLF;
        return responseStatusLine.getBytes();
    }

    private byte[] getResponseHeaders() {
        StringBuilder headersBuiltString = new StringBuilder();
        headers.forEach(e -> headersBuiltString.append(e.toString()).append(HttpResponseConstants.CRLF));
        if (cookies.size() > 0) {
            HttpHeader cookiesHeader = getCookiesHeader();
            headersBuiltString.append(cookiesHeader.toString()).append(HttpResponseConstants.CRLF);
        }
        headersBuiltString.append(HttpResponseConstants.CRLF);
        String headersString = headersBuiltString.toString();
        return headersString.getBytes();
    }

    HttpHeader getCookiesHeader() {
        StringJoiner joiner = new StringJoiner("; ");
        for (Cookie cookie : cookies) {
            joiner.add(cookieToString(cookie));
        }
        String headerName = HttpHeaderName.COOKIE.getName();
        String headerValue = joiner.toString();

        return new HttpHeader(headerName, headerValue);
    }

    private String cookieToString(Cookie cookie) {
        return cookie.getName() + "=" + cookie.getValue();
    }

}

package com.ibrezhneva.webby.server.entity.model;

import com.ibrezhneva.webby.server.entity.adapter.HttpServletResponseAdapter;
import com.ibrezhneva.webby.server.entity.http.HttpHeader;
import com.ibrezhneva.webby.server.entity.http.HttpHeaderName;
import com.ibrezhneva.webby.server.entity.http.HttpStatus;
import lombok.Getter;
import lombok.Setter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@Setter
public class AppServletResponse extends HttpServletResponseAdapter {
    private AppServletOutputStream outputStream;
    private PrintWriter writer;
    private String contentType;
    @Getter
    private List<HttpHeader> headers = new ArrayList<>();
    @Getter
    private List<Cookie> cookies = new ArrayList<>();
    @Getter
    private HttpStatus httpStatus;
    private int status;

    @Override
    public void addCookie(Cookie cookie) {
        cookies.add(cookie);
    }

    @Override
    public void sendRedirect(String location) throws IOException {
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
        return outputStream;
    }

    @Override
    public PrintWriter getWriter() {
        return writer;
    }

    @Override
    public void setContentType(String type) {
        setHeader(HttpHeaderName.CONTENT_TYPE.getName(), type);
    }
}

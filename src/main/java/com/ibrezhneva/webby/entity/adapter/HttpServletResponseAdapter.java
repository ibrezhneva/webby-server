package com.ibrezhneva.webby.entity.adapter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

public class HttpServletResponseAdapter implements HttpServletResponse {
    @Override
    public void addCookie(Cookie cookie) {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public boolean containsHeader(String name) {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public String encodeURL(String url) {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public String encodeRedirectURL(String url) {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public String encodeUrl(String url) {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public String encodeRedirectUrl(String url) {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public void sendError(int sc) throws IOException {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public void setDateHeader(String name, long date) {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public void addDateHeader(String name, long date) {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public void setHeader(String name, String value) {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public void addHeader(String name, String value) {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public void setIntHeader(String name, int value) {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public void addIntHeader(String name, int value) {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public void setStatus(int sc) {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public void setStatus(int sc, String sm) {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public int getStatus() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public String getHeader(String name) {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public Collection<String> getHeaders(String name) {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public Collection<String> getHeaderNames() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public String getCharacterEncoding() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public String getContentType() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public void setCharacterEncoding(String charset) {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public void setContentLength(int len) {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public void setContentLengthLong(long len) {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public void setContentType(String type) {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public void setBufferSize(int size) {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public int getBufferSize() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public void flushBuffer() throws IOException {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public void resetBuffer() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public boolean isCommitted() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public void setLocale(Locale loc) {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public Locale getLocale() {
        throw new UnsupportedOperationException("Method is not supported");
    }
}

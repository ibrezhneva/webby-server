package com.ibrezhneva.webby.server.entity.adapter;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

public class HttpServletRequestAdapter implements HttpServletRequest {
    @Override
    public String getAuthType() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public Cookie[] getCookies() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public long getDateHeader(String name) {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public String getHeader(String name) {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public int getIntHeader(String name) {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public String getMethod() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public String getPathInfo() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public String getPathTranslated() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public String getContextPath() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public String getQueryString() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public String getRemoteUser() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public boolean isUserInRole(String role) {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public Principal getUserPrincipal() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public String getRequestedSessionId() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public String getRequestURI() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public StringBuffer getRequestURL() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public String getServletPath() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public HttpSession getSession(boolean create) {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public HttpSession getSession() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public String changeSessionId() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public void login(String username, String password) throws ServletException {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public void logout() throws ServletException {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public Part getPart(String name) throws IOException, ServletException {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public Object getAttribute(String name) {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public String getCharacterEncoding() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public int getContentLength() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public long getContentLengthLong() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public String getContentType() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public String getParameter(String name) {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public Enumeration<String> getParameterNames() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public String[] getParameterValues(String name) {
        return new String[0];
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public String getProtocol() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public String getScheme() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public String getServerName() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public int getServerPort() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public BufferedReader getReader() throws IOException {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public String getRemoteAddr() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public String getRemoteHost() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public void setAttribute(String name, Object o) {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public void removeAttribute(String name) {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public Locale getLocale() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public Enumeration<Locale> getLocales() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public boolean isSecure() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public String getRealPath(String path) {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public int getRemotePort() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public String getLocalName() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public String getLocalAddr() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public int getLocalPort() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public ServletContext getServletContext() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public boolean isAsyncStarted() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public boolean isAsyncSupported() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public AsyncContext getAsyncContext() {
        throw new UnsupportedOperationException("Method is not supported");
    }

    @Override
    public DispatcherType getDispatcherType() {
        throw new UnsupportedOperationException("Method is not supported");
    }
}

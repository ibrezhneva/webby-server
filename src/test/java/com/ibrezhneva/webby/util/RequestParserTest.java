package com.ibrezhneva.webby.util;

import com.ibrezhneva.webby.entity.model.AppServletRequest;
import com.ibrezhneva.webby.entity.model.RequestInputStream;
import lombok.Cleanup;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class RequestParserTest {

    private static final String requestString = "GET /app/index.html HTTP/1.1\r\n" +
            "Host: localhost:3000\r\n" +
            "Connection: keep-alive\r\n" +
            "Upgrade-Insecure-Requests: 1\r\n" +
            "Content-Type: text/html; charset=utf-8\r\n" +
            "Content-Length: 5\r\n" +
            "Cookie: name=F\r\n" +
            "\r\n" +
            "12345";

    @Test
    @DisplayName("Verify Uri, Method and Protocol")
    void testInjectMethodAndProtocol() {
        AppServletRequest request = new AppServletRequest();
        String requestLine = "GET /app/index.html HTTP/1.1";
        RequestParser.injectMethodAndProtocol(request, requestLine);
        assertEquals("GET", request.getMethod());
        assertEquals("HTTP/1.1", request.getProtocol());
    }

    @Test
    @DisplayName("Verify URI and Query string")
    void testInjectQueryStringAndURI() {
        AppServletRequest request = new AppServletRequest();
        String uri = "/app/users/default?fullname=Fadi%20Fakhouri#top";
        RequestParser.injectQueryStringAndURI(request, uri);
        assertEquals("/app/users/default", request.getRequestURI());
        assertEquals("fullname=Fadi%20Fakhouri", request.getQueryString());
    }

    @Test
    @DisplayName("Verify web application Name and servlet Path")
    void testInjectWebAppNameAndServletPath() {
        AppServletRequest request = new AppServletRequest();
        request.setUri("/app/products/all");
        RequestParser.injectWebAppNameAndServletPath(request);
        assertEquals("app", request.getWebAppName());
        assertEquals("/products/all", request.getServletPath());
    }

    @Test
    @DisplayName("Verify Parameters")
    void testInjectParameters() {
        AppServletRequest request = new AppServletRequest();
        request.setQueryString("name1=value1&name2=value2&name2=value3");
        RequestParser.injectParameters(request);
        String[] name2ParamValues = {"value2", "value3"};
        assertArrayEquals(name2ParamValues, request.getParameterValues("name2"));
        assertEquals("value1", request.getParameter("name1"));
    }

    @Test
    @DisplayName("Verify Headers")
    void testInjectHeaders() throws IOException {
        AppServletRequest request = new AppServletRequest();
        byte[] requestStringBytes = requestString.getBytes();
        @Cleanup InputStream inputStream = new ByteArrayInputStream(requestStringBytes);
        RequestInputStream reader = new RequestInputStream(inputStream);
        reader.readLine();
        RequestParser.injectHeaders(request, reader);

        assertEquals("localhost:3000", request.getHeader("Host"));
        assertEquals("keep-alive", request.getHeader("Connection"));
        assertEquals("1", request.getHeader("Upgrade-Insecure-Requests"));
        assertEquals("text/html; charset=utf-8", request.getHeader("Content-Type"));
        assertEquals("5", request.getHeader("Content-Length"));
    }

    @Test
    @DisplayName("Verify Cookies")
    void testInjectCookies() {
        AppServletRequest request = new AppServletRequest();
        String cookies = "name=value; name2=value2; name3=value3";
        RequestParser.injectCookies(request, cookies);
        assertEquals(3, request.getCookies().length);
    }

    @Test
    @DisplayName("Verify request body")
    void testParseRequest() throws IOException {
        byte[] requestStringBytes = requestString.getBytes();
        try (InputStream inputStream = new ByteArrayInputStream(requestStringBytes)) {
            AppServletRequest appServletRequest = RequestParser.parseRequest(inputStream);
            assertNotNull(appServletRequest);
            String actualBody = IOUtils.toString(appServletRequest.getInputStream(), StandardCharsets.UTF_8);
            assertEquals("12345", actualBody);
        }
    }
}

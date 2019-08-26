package com.ibrezhneva.webby.server.entity.model;

import com.ibrezhneva.webby.server.entity.http.HttpHeader;
import org.junit.jupiter.api.Test;

import javax.servlet.http.Cookie;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WebAppTest {

    @Test
    void testGetCookiesHeader() {
        AppServletResponse response = new AppServletResponse(new ByteArrayOutputStream());
        List<Cookie> cookies = new ArrayList<>();
        cookies.add(new Cookie("cookie1", "value1"));
        cookies.add(new Cookie("cookie2", "value2"));
        cookies.add(new Cookie("cookie3", "value3"));
        response.setCookies(cookies);

        String cookieHeaderValue = "cookie1=value1; cookie2=value2; cookie3=value3";
        HttpHeader expectedHttpHeader = new HttpHeader("Cookie", cookieHeaderValue);
        HttpHeader actualHttpHeader = response.getCookiesHeader();
        assertEquals(expectedHttpHeader, actualHttpHeader);
    }
}
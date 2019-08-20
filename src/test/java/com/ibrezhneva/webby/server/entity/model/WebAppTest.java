package com.ibrezhneva.webby.server.entity.model;

import com.ibrezhneva.webby.server.entity.http.HttpHeader;
import org.junit.jupiter.api.Test;

import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WebAppTest {

    @Test
    void testGetCookiesHeader() {
        WebApp webApp = new WebApp("test");
        AppServletResponse response = new AppServletResponse();
        List<Cookie> cookies = new ArrayList<>();
        cookies.add(new Cookie("cookie1", "value1"));
        cookies.add(new Cookie("cookie2", "value2"));
        cookies.add(new Cookie("cookie3", "value3"));
        response.setCookies(cookies);

        String cookieHeaderValue = "cookie1=value1; cookie2=value2; cookie3=value3";
        HttpHeader expectedHttpHeader = new HttpHeader("Cookie", cookieHeaderValue);
        HttpHeader actualHttpHeader = webApp.getCookiesHeader(response);
        assertEquals(expectedHttpHeader, actualHttpHeader);
    }
}
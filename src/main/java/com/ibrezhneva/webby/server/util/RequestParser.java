package com.ibrezhneva.webby.server.util;

import com.ibrezhneva.webby.server.entity.http.HttpHeader;
import com.ibrezhneva.webby.server.entity.http.HttpHeaderName;
import com.ibrezhneva.webby.server.entity.http.HttpMethod;
import com.ibrezhneva.webby.server.entity.http.HttpStatus;
import com.ibrezhneva.webby.server.entity.model.AppServletInputStream;
import com.ibrezhneva.webby.server.entity.model.AppServletRequest;
import com.ibrezhneva.webby.server.exception.ServerException;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public abstract class RequestParser {
    public static AppServletRequest parseRequest(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String line = reader.readLine();
            AppServletRequest request = new AppServletRequest();
            injectUriAndMethodAndProtocol(request, line);
            injectWebAppNameAndServletPath(request);
            injectQueryString(request);
            injectHeaders(request, reader);
            return request;
        } catch (Exception e) {
            log.error("Error during request parsing", e);
            throw new ServerException(HttpStatus.BAD_REQUEST, "Error during request parsing");
        }
    }

    static void injectUriAndMethodAndProtocol(AppServletRequest request, String requestLine) {
        String[] splitString = requestLine.split(" ");
        HttpMethod httpMethod = HttpMethod.getByName(splitString[0]);
        request.setHttpMethod(httpMethod);
        request.setUri(splitString[1]);
        request.setProtocol(splitString[2]);
    }

    static void injectWebAppNameAndServletPath(AppServletRequest request) {
        String[] splitUri = request.getRequestURI().split("/", 3);
        request.setWebAppName(splitUri[1]);
        if(splitUri.length < 3) {
            request.setServletPath("/");
            return;
        }
        request.setServletPath("/" + splitUri[2]);
    }

    static void injectQueryString(AppServletRequest request) {
        String uri = request.getRequestURI();
        Pattern pattern = Pattern.compile("\\?([^\\#]*)");
        Matcher matcher = pattern.matcher(uri);
        if (matcher.find()) {
            request.setQueryString(matcher.group(1));
        }
    }

    static void injectHeaders(AppServletRequest request, BufferedReader reader) throws IOException {
        List<HttpHeader> headers = new ArrayList<>();
        String line;
        while (!(line = reader.readLine()).isEmpty()) {
            String[] splitString = line.split(": ");
            headers.add(new HttpHeader(splitString[0], splitString[1]));
        }
        request.setHeaders(headers);
    }

}

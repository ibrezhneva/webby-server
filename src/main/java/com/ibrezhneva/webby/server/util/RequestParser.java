package com.ibrezhneva.webby.server.util;

import com.ibrezhneva.webby.server.entity.http.HttpHeader;
import com.ibrezhneva.webby.server.entity.http.HttpHeaderName;
import com.ibrezhneva.webby.server.entity.http.HttpMethod;
import com.ibrezhneva.webby.server.entity.http.HttpStatus;
import com.ibrezhneva.webby.server.entity.model.AppServletInputStream;
import com.ibrezhneva.webby.server.entity.model.AppServletRequest;
import com.ibrezhneva.webby.server.entity.model.RequestInputStream;
import com.ibrezhneva.webby.server.exception.ServerException;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.Cookie;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public abstract class RequestParser {
    public static AppServletRequest parseRequest(InputStream inputStream) {
        RequestInputStream reader = new RequestInputStream(inputStream);
        try {
            String line = reader.readLine();
            AppServletRequest request = new AppServletRequest();

            injectMethodAndProtocol(request, line);
            injectWebAppNameAndServletPath(request);
            injectParameters(request);
            injectHeaders(request, reader);

            if (request.getIntHeader(HttpHeaderName.CONTENT_LENGTH.getName()) > 0 ||
                    (request.getHeader(HttpHeaderName.TRANSFER_ENCODING.getName()) != null)) {
                injectInputStream(request, inputStream);
            }
            return request;
        } catch (Exception e) {
            log.error("Error during request parsing", e);
            throw new ServerException(HttpStatus.BAD_REQUEST, "Error during request parsing");
        }
    }

    static void injectMethodAndProtocol(AppServletRequest request, String requestLine) {
        String[] splitString = requestLine.split(" ");
        HttpMethod httpMethod = HttpMethod.getByName(splitString[0]);
        request.setHttpMethod(httpMethod);

        injectQueryStringAndURI(request, splitString[1]);
        request.setProtocol(splitString[2]);
    }

    static void injectQueryStringAndURI(AppServletRequest request, String requestURI) {
        String[] splitUri = requestURI.split("\\?");
        request.setUri(splitUri[0]);
        Pattern pattern = Pattern.compile("\\?([^\\#]*)");
        Matcher matcher = pattern.matcher(requestURI);
        if (matcher.find()) {
            request.setQueryString(matcher.group(1));
        }
    }

    static void injectWebAppNameAndServletPath(AppServletRequest request) {
        String[] splitUri = request.getRequestURI().split("/", 3);
        request.setWebAppName(splitUri[1]);
        if (splitUri.length < 3) {
            request.setServletPath("/");
            return;
        }
        request.setServletPath("/" + splitUri[2]);
    }

    static void injectParameters(AppServletRequest request) {
        String queryString = request.getQueryString();
        Map<String, String[]> parameters = new HashMap<>();
        if (queryString == null) {
            request.setParameters(parameters);
            return;
        }
        Map<String, List<String>> parameterValuesListMap = new HashMap<>();
        String[] splitParameters = queryString.split("&");
        for (String queryParameter : splitParameters) {
            String[] splitParameter = queryParameter.split("=");

            String key = splitParameter[0];
            String value = splitParameter[1];
            List<String> paramValueList = Optional.ofNullable(parameterValuesListMap.get(key))
                    .orElse(new ArrayList<>());
            paramValueList.add(value);
            parameterValuesListMap.put(key, paramValueList);
        }
        parameterValuesListMap.forEach((k, v) -> parameters.put(k, v.toArray(new String[v.size()])));
        request.setParameters(parameters);
    }

    static void injectHeaders(AppServletRequest request, RequestInputStream reader) throws IOException {
        List<HttpHeader> headers = new ArrayList<>();
        String line;
        while (!(line = reader.readLine()).isEmpty()) {
            String[] splitString = line.split(": ");
            if (splitString[0].equalsIgnoreCase(HttpHeaderName.COOKIE.getName())) {
                injectCookies(request, splitString[1]);
            }
            headers.add(new HttpHeader(splitString[0], splitString[1]));
        }
        request.setHeaders(headers);
    }

    static void injectCookies(AppServletRequest request, String cookies) {
        String[] splitCookieString = cookies.split("; ");
        Cookie[] cookieArray = new Cookie[splitCookieString.length];
        for (int i = 0; i < splitCookieString.length; i++) {
            String[] splitNameValue = splitCookieString[i].split("=");
            cookieArray[i] = new Cookie(splitNameValue[0], splitNameValue[1]);
        }
        request.setCookies(cookieArray);
    }

    private static void injectInputStream(AppServletRequest request, InputStream inputStream) throws IOException {
        int nReady = inputStream.available();
        if (nReady > 0) {
            byte[] bytes = new byte[nReady];
            inputStream.read(bytes);
            InputStream bodyInputStream = new ByteArrayInputStream(bytes);
            AppServletInputStream servletInputStream = new AppServletInputStream(bodyInputStream);
            request.setInputStream(servletInputStream);
        }
    }

}

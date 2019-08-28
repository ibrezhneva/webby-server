package com.ibrezhneva.webby.util;

import com.ibrezhneva.webby.entity.http.HttpHeader;
import com.ibrezhneva.webby.entity.http.HttpHeaderName;
import com.ibrezhneva.webby.entity.http.HttpMethod;
import com.ibrezhneva.webby.entity.http.HttpStatus;
import com.ibrezhneva.webby.entity.model.AppServletInputStream;
import com.ibrezhneva.webby.entity.model.AppServletRequest;
import com.ibrezhneva.webby.entity.model.RequestInputStream;
import com.ibrezhneva.webby.exception.ServerException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.ChunkedInputStream;

import javax.servlet.http.Cookie;
import java.io.BufferedInputStream;
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
            injectQueryStringAndURI(request);
            injectWebAppNameAndServletPath(request);
            injectParameters(request);
            injectHeaders(request, reader);

            if (HttpMethod.POST.getName().equals(request.getMethod())) {
                int n;
                if ((n = request.getIntHeader(HttpHeaderName.CONTENT_LENGTH.getName())) > 0) {
                    injectInputStream(request, inputStream, n);
                } else if (request.getHeader(HttpHeaderName.TRANSFER_ENCODING.getName()) != null) {
                    injectChunkedInputStream(request, inputStream);
                }
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
        request.setAbsoluteUri(splitString[1]);
        request.setProtocol(splitString[2]);
    }

    static void injectQueryStringAndURI(AppServletRequest request) {
        String requestURI = request.getAbsoluteUri();
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

    private static void injectInputStream(AppServletRequest request, InputStream inputStream, int n) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        byte[] bytes = new byte[n];
        bufferedInputStream.read(bytes);
        InputStream bodyInputStream = new ByteArrayInputStream(bytes);
        AppServletInputStream servletInputStream = new AppServletInputStream(bodyInputStream);
        request.setInputStream(servletInputStream);
    }

    private static void injectChunkedInputStream(AppServletRequest request, InputStream inputStream) throws IOException {
        ChunkedInputStream chunkedInputStream = new ChunkedInputStream(inputStream);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(chunkedInputStream);
        AppServletInputStream servletInputStream = new AppServletInputStream(bufferedInputStream);
        request.setInputStream(servletInputStream);
    }

}
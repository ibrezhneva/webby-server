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
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import javax.servlet.http.Cookie;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
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
            injectUriParameters(request);
            injectHeaders(request, reader);

            if (HttpMethod.POST.getName().equals(request.getMethod())) {
                int n;
                if ((n = request.getContentLength()) > 0) {
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
            request.setRootRedirect(true);
            return;
        }
        request.setServletPath("/" + splitUri[2]);
    }

    static void injectUriParameters(AppServletRequest request) {
        String queryString = request.getQueryString();
        Map<String, String[]> parameters = parseParameters(queryString);
        request.setParameters(parameters);
    }

    static void injectHeaders(AppServletRequest request, RequestInputStream reader) throws IOException {
        List<HttpHeader> headers = new ArrayList<>();
        String line;
        while (!(line = reader.readLine()).isEmpty()) {
            String[] splitString = line.split(": ");
            if (splitString[0].equalsIgnoreCase(HttpHeaderName.COOKIE.getName())) {
                injectCookies(request, splitString[1]);
            } else if (splitString[0].equalsIgnoreCase(HttpHeaderName.CONTENT_TYPE.getName())) {
                injectCharset(request, splitString[1]);
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

    static void injectCharset(AppServletRequest request, String contentType) {
        String charset = "charset=";
        if (!contentType.contains(charset)) {
            return;
        }
        String[] splitContentTypeString = contentType.split(charset);
        request.setCharset(splitContentTypeString[1]);
    }

    private static void injectInputStream(AppServletRequest request, InputStream inputStream, int n) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        byte[] bytes = new byte[n];
        bufferedInputStream.read(bytes);
        if (request.getContentType().contains("application/x-www-form-urlencoded")) {
            injectBodyParameters(request, bytes);
        }
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

    private static void injectBodyParameters(AppServletRequest request, byte[] bytes) {
        String charset = Optional.ofNullable(request.getCharacterEncoding()).orElse("UTF-8");
        String paramString = new String(bytes, Charset.forName(charset));
        Map<String, String[]> parameters = parseParameters(paramString);
        request.setParameters(parameters);
    }

    private static Map<String, String[]> parseParameters(String paramString) {
        Map<String, String[]> parametersMap = new HashMap<>();
        if (paramString == null) {
            return parametersMap;
        }
        List<NameValuePair> parameters = URLEncodedUtils.parse(paramString, Charset.forName("UTF-8"));
        Map<String, List<String>> parameterValuesListMap = new HashMap<>();

        for (NameValuePair parameter : parameters) {
            String key = parameter.getName();
            String value = parameter.getValue();

            List<String> paramValueList = Optional.ofNullable(parameterValuesListMap.get(key)).orElse(new ArrayList<>());
            paramValueList.add(value);
            parameterValuesListMap.put(key, paramValueList);
        }
        parameterValuesListMap.forEach((k, v) -> parametersMap.put(k, v.toArray(new String[v.size()])));
        return parametersMap;
    }

}
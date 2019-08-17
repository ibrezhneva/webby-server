package com.ibrezhneva.webby.server.service;


import com.ibrezhneva.webby.server.entity.http.HttpResponseConstants;
import com.ibrezhneva.webby.server.entity.model.*;
import com.ibrezhneva.webby.server.entity.http.HttpStatus;
import com.ibrezhneva.webby.server.exception.ServerException;
import com.ibrezhneva.webby.server.util.RequestParser;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;

@Slf4j
public class RequestHandler implements Runnable {
    private Socket httpSocket;
    private WebAppContainer webAppContainer;

    public RequestHandler(Socket httpSocket, WebAppContainer webAppContainer) {
        this.httpSocket = httpSocket;
        this.webAppContainer = webAppContainer;
    }

    @Override
    public void run() {
        handle();
    }

    @SneakyThrows
    private void handle() {
        @Cleanup Socket socket = httpSocket;
        while (!socket.isClosed()) {
            try (InputStream inputStream = socket.getInputStream();
                 OutputStream outputStream = socket.getOutputStream()) {
                if (inputStream.available() > 0) {
                    try {
                        AppServletRequest request = RequestParser.parseRequest(inputStream);
                        AppServletResponse response = createAppServletResponse(outputStream);

                        String requestWebAppName = request.getWebAppName();
                        WebApp webApp = webAppContainer.getWebApp(requestWebAppName)
                                .orElseThrow(() -> new ServerException(HttpStatus.NOT_FOUND, "There is no web application for " + requestWebAppName));
                        webApp.process(request, response);
                    } catch (ServerException e) {
                        writeResponseDirectly(outputStream, e.getHttpStatus(), e.getMessage());
                    } catch (Exception e) {
                        writeResponseDirectly(outputStream, HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
                    }
                }
            }
        }
    }

    private AppServletResponse createAppServletResponse(OutputStream outputStream) {
        AppServletResponse response = new AppServletResponse();
        AppServletOutputStream servletOutputStream = new AppServletOutputStream(outputStream);
        response.setOutputStream(servletOutputStream);

        OutputStreamWriter streamWriter = new OutputStreamWriter(servletOutputStream);
        response.setWriter(new PrintWriter(streamWriter, true));
        return response;
    }

    @SneakyThrows
    private void writeResponseDirectly(OutputStream outputStream, HttpStatus httpStatus, String message) {
        @Cleanup BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
        String statusLine = HttpResponseConstants.HTTP_VERSION + " " + httpStatus.toString();
        bufferedOutputStream.write(statusLine.getBytes());
        bufferedOutputStream.write(HttpResponseConstants.CRLF_BYTES);
        bufferedOutputStream.write(HttpResponseConstants.CRLF_BYTES);
        bufferedOutputStream.write(message.getBytes());
    }
}

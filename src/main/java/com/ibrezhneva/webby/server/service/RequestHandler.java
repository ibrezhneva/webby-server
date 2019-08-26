package com.ibrezhneva.webby.server.service;


import com.ibrezhneva.webby.server.entity.http.HttpResponseConstants;
import com.ibrezhneva.webby.server.entity.http.HttpStatus;
import com.ibrezhneva.webby.server.entity.model.AppServletRequest;
import com.ibrezhneva.webby.server.entity.model.AppServletResponse;
import com.ibrezhneva.webby.server.entity.model.WebApp;
import com.ibrezhneva.webby.server.entity.model.WebAppContainer;
import com.ibrezhneva.webby.server.exception.ServerException;
import com.ibrezhneva.webby.server.util.RequestParser;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.Socket;
import java.util.Optional;

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
        InputStream inputStream = httpSocket.getInputStream();
        OutputStream outputStream = httpSocket.getOutputStream();
        while (inputStream.available() > 0) {
            try {
                AppServletRequest request = RequestParser.parseRequest(inputStream);
                AppServletResponse response = new AppServletResponse(outputStream);

                String requestWebAppName = request.getWebAppName();
                Optional<WebApp> webApp = webAppContainer.getWebApp(requestWebAppName);
                if (webApp.isPresent()) {
                    webApp.get().process(request, response);
                } else {
                    processResourceByPath(request, outputStream);
                }
            } catch (ServerException e) {
                writeResponseDirectly(outputStream, e.getHttpStatus(), e.getMessage().getBytes());
            } catch (Exception e) {
                writeResponseDirectly(outputStream, HttpStatus.INTERNAL_SERVER_ERROR, e.toString().getBytes());
            }
        }
    }

    @SneakyThrows
    private void writeResponseDirectly(OutputStream outputStream, HttpStatus httpStatus, byte[] bytes) {
        @Cleanup BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
        String statusLine = HttpResponseConstants.HTTP_VERSION + " " + httpStatus.toString();
        bufferedOutputStream.write(statusLine.getBytes());
        bufferedOutputStream.write(HttpResponseConstants.CRLF_BYTES);
        bufferedOutputStream.write(HttpResponseConstants.CRLF_BYTES);
        bufferedOutputStream.write(bytes);
    }

    private void processResourceByPath(AppServletRequest request, OutputStream outputStream) {
        File resource = new File(request.getRequestURI().substring(1));
        try (FileInputStream inputStream = new FileInputStream(resource.getCanonicalPath())) {
            byte[] bytes = IOUtils.toByteArray(inputStream);
            writeResponseDirectly(outputStream, HttpStatus.OK, bytes);
        } catch (IOException e) {
            throw new ServerException(HttpStatus.NOT_FOUND, "There is no resource for " + resource.getPath());
        }
    }
}

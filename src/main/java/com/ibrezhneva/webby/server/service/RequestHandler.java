package com.ibrezhneva.webby.server.service;


import com.ibrezhneva.webby.server.entity.model.WebAppContainer;
import com.ibrezhneva.webby.server.entity.http.HttpStatus;
import com.ibrezhneva.webby.server.entity.model.AppServletOutputStream;
import com.ibrezhneva.webby.server.entity.model.AppServletRequest;
import com.ibrezhneva.webby.server.entity.model.AppServletResponse;
import com.ibrezhneva.webby.server.exception.ServerException;
import com.ibrezhneva.webby.server.util.RequestParser;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;

@Slf4j
public class RequestHandler implements Runnable {
    private Socket socket;
    private WebAppContainer webAppContainer;

    public RequestHandler(Socket socket, WebAppContainer webAppContainer) {
        this.socket = socket;
        this.webAppContainer = webAppContainer;
    }

    @Override
    public void run() {
        handle();
    }

    private void handle() {
        try (InputStream inputStream = socket.getInputStream();
             OutputStream outputStream = socket.getOutputStream()) {
            try {
                AppServletRequest request = RequestParser.parseRequest(inputStream);
                AppServletResponse response = getInitAppServletResponse(outputStream);
                webAppContainer.getWebApp(request.getWebAppName()).process(request, response);
            } catch (ServerException e) {
                writeResponseDirectly(outputStream, e.getHttpStatus(), e.getMessage());
            } catch (Exception e) {
                writeResponseDirectly(outputStream, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } catch (IOException e) {
            log.error("Error during getting stream from socket", e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                log.error("Error during socket closure", e);
            }
        }
    }

    private AppServletResponse getInitAppServletResponse(OutputStream outputStream) {
        AppServletResponse response = new AppServletResponse();
        AppServletOutputStream servletOutputStream = new AppServletOutputStream(outputStream);
        response.setOutputStream(servletOutputStream);

        OutputStreamWriter streamWriter = new OutputStreamWriter(servletOutputStream);
        response.setWriter(new PrintWriter(streamWriter, true));
        return response;
    }

    private void writeResponseDirectly(OutputStream outputStream, HttpStatus httpStatus, String message) {
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
        try {
            String statusLine = "HTTP/1.1" + " " + httpStatus.toString();
            bufferedOutputStream.write(statusLine.getBytes());
            bufferedOutputStream.write("\r\n\r\n".getBytes());
            bufferedOutputStream.write(message.getBytes());
        } catch (IOException e) {
            log.error("Bad luck =(", e);
        }
    }
}

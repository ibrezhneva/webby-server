package com.ibrezhneva.web.app.servlet;

import com.ibrezhneva.web.app.ServiceLocator;
import com.ibrezhneva.web.app.service.MessageService;
import com.ibrezhneva.web.app.service.impl.DefaultMessageService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class PostMessageServlet extends HttpServlet {

    private MessageService messageService = ServiceLocator.getService(DefaultMessageService.class);


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String responseMessage;
        try {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.addHeader("Content-Type", "text/html; charset=utf-8");
            responseMessage = messageService.addGreeting(getBodyMessage(req.getInputStream()));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseMessage = e.getMessage();
        }
        resp.getWriter().write(responseMessage);
    }

    private String getBodyMessage(InputStream inputStream) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }
}

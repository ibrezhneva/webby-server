package com.ibrezhneva.web.app.servlet;

import com.ibrezhneva.web.app.ServiceLocator;
import com.ibrezhneva.web.app.service.MessageService;
import com.ibrezhneva.web.app.service.impl.DefaultMessageService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GetMessageServlet extends HttpServlet {

    private MessageService messageService = ServiceLocator.getService(DefaultMessageService.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.addHeader("Content-Type", "text/html; charset=utf-8");
        resp.addHeader("Test-Header", "test-header123");
        String message = messageService.getMessage();
        resp.getWriter().write(message);
    }
}

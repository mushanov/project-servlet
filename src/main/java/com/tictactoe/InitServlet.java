package com.tictactoe;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "InitServlet", value = "/start")
public class InitServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp) throws ServletException, IOException {

        // Создание новой сессии
        HttpSession currentSession = req.getSession(true);

        // Создание игрового поля
        Field field = new Field();
        Map<Integer, Sign> fieldData = field.getField();

        // Получение списка значений поля
        List<Sign> data = field.getFieldData();

        // Сохраняем поле и данные в сессию
        currentSession.setAttribute("field", field);
        currentSession.setAttribute("data", data);

        // Форвард на JSP
        getServletContext()
                .getRequestDispatcher("/index.jsp")
                .forward(req, resp);
    }
}

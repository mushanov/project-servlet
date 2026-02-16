package com.tictactoe;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "LogicServlet", value = "/logic")
public class LogicServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp) throws ServletException, IOException {

        // Текущая сессия
        HttpSession currentSession = req.getSession();

        // Игровое поле из сессии
        Field field = extractField(currentSession);

        // Индекс кликнутой ячейки
        int index = getSelectedIndex(req);
        Sign currentSign = field.getField().get(index);

        // Если ячейка уже занята — просто перерисовываем страницу
        if (Sign.EMPTY != currentSign) {
            RequestDispatcher dispatcher =
                    getServletContext().getRequestDispatcher("/index.jsp");
            dispatcher.forward(req, resp);
            return;
        }

        // Ход крестика
        field.getField().put(index, Sign.CROSS);

        // Проверяем, не победил ли крестик
        if (checkWin(resp, currentSession, field)) {
            return;
        }

        // Ход нолика (AI)
        int emptyFieldIndex = field.getEmptyFieldIndex();

        if (emptyFieldIndex >= 0) {
            field.getField().put(emptyFieldIndex, Sign.NOUGHT);

            // Проверяем победу ноликов
            if (checkWin(resp, currentSession, field)) {
                return;
            }
        } else {
            // Пустых клеток нет и победителя нет — ничья
            currentSession.setAttribute("draw", true);

            List<Sign> data = field.getFieldData();
            currentSession.setAttribute("data", data);

            resp.sendRedirect("/index.jsp");
            return;
        }

        // Обновляем данные в сессии и редиректим
        List<Sign> data = field.getFieldData();
        currentSession.setAttribute("data", data);
        currentSession.setAttribute("field", field);

        resp.sendRedirect("/index.jsp");
    }

    /**
     * Проверка победы.
     */
    private boolean checkWin(HttpServletResponse response,
                             HttpSession currentSession,
                             Field field) throws IOException {

        Sign winner = field.checkWin();
        if (Sign.CROSS == winner || Sign.NOUGHT == winner) {
            currentSession.setAttribute("winner", winner);

            List<Sign> data = field.getFieldData();
            currentSession.setAttribute("data", data);

            response.sendRedirect("/index.jsp");
            return true;
        }
        return false;
    }

    private int getSelectedIndex(HttpServletRequest request) {
        String click = request.getParameter("click");
        boolean isNumeric = click != null && click.chars().allMatch(Character::isDigit);
        return isNumeric ? Integer.parseInt(click) : 0;
    }

    private Field extractField(HttpSession currentSession) {
        Object fieldAttribute = currentSession.getAttribute("field");
        if (!(fieldAttribute instanceof Field)) {
            currentSession.invalidate();
            throw new RuntimeException("Session is broken, try one more time");
        }
        return (Field) fieldAttribute;
    }
}

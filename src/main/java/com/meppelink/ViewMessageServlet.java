package com.meppelink;

import com.meppelink.Discord.DiscordMessage;
import com.meppelink.data_access.DiscordDAO_MySQL;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@WebServlet(name = "ViewMessageServlet", value = "/viewMessages")
public class ViewMessageServlet extends HttpServlet {
    private ArrayList<DiscordMessage> messages = new ArrayList<>();
    private DiscordDAO_MySQL dao = new DiscordDAO_MySQL();


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        messages = dao.selectAllMessages();
        request.setAttribute("messages", messages);
        request.getRequestDispatcher("/WEB-INF/viewMessages.jsp").forward(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}

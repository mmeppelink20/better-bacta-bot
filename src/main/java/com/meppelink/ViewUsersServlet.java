package com.meppelink;

import com.meppelink.Discord.DiscordMessage;
import com.meppelink.Discord.DiscordUser;
import com.meppelink.data_access.DiscordDAO_MySQL;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.ArrayList;

@WebServlet(name = "ViewUsersServlet", value = "/viewUsers")
public class ViewUsersServlet extends HttpServlet {
    private ArrayList<DiscordUser> users = new ArrayList<>();
    private DiscordDAO_MySQL dao = new DiscordDAO_MySQL();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        users = dao.selectAllDiscordUsers();
        System.out.println(users);
        request.setAttribute("users", users);
        request.getRequestDispatcher("/WEB-INF/viewUsers.jsp").forward(request,response);
    }
}

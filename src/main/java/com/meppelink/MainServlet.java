package com.meppelink;

import com.meppelink.Discord.DiscordBot;
import com.meppelink.data_access.DiscordDAO_MySQL;
import net.dv8tion.jda.api.entities.Member;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "MainServlet", value = "/main")
public class MainServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("index.jsp").forward(request,response);
        DiscordBot bot = (DiscordBot) getServletContext().getAttribute("discordBot");
        // Use the bot instance as needed
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String loginButtonClicked = request.getParameter("loginButton");
        String registerButtonClicked = request.getParameter("registerButton");
        String logoutButtonClicked = request.getParameter("logoutButton");
        String viewMessageButtonClicked = request.getParameter("viewMessageButton");
        if (loginButtonClicked != null && loginButtonClicked.equals("true")) {
            response.sendRedirect("login");
        }
        if (registerButtonClicked != null && registerButtonClicked.equals("true")) {
            response.sendRedirect("signup");
        }
        if(logoutButtonClicked != null && logoutButtonClicked.equals("true")) {
            HttpSession session = request.getSession();
            session.invalidate();
            response.sendRedirect("main");
        }
        if(viewMessageButtonClicked != null && viewMessageButtonClicked.equals("true")) {
            response.sendRedirect("viewMessages");
        }
    }
}

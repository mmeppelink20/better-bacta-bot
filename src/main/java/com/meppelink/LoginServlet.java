package com.meppelink;

import com.meppelink.User.User;
import com.meppelink.data_access.UserDAO_MySQL;
import org.mindrot.jbcrypt.BCrypt;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "LoginServlet", value = "/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("loginFailed", false);
        request.getRequestDispatcher("WEB-INF/login.jsp").forward(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("loginFailed", false);
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        UserDAO_MySQL dao = new UserDAO_MySQL();
        User user = dao.getUser(email);
        if(email == null || email.equals("") ||
                password == null || password.equals("") ||
                user == null
        ) {
            request.setAttribute("loginFailed", true);
            request.getRequestDispatcher("WEB-INF/login.jsp").forward(request, response);
            return;
        } else {
            // User was found
            if(BCrypt.checkpw(password, String.valueOf(user.getPassword()))) {
                // Password is correct
                HttpSession session = request.getSession();
                request.changeSessionId();
                user.unsetPassword();
                session.setAttribute("user", user);
                response.sendRedirect("main"); // change for final project
                return;
            } else {
                // Password is incorrect
                request.setAttribute("loginFailed", true);
                request.getRequestDispatcher("WEB-INF/login.jsp").forward(request, response);
                return;
            }

        }

    }

}

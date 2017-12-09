package com.weixin.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/send")
public class SendServlet extends HttpServlet{
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session=req.getSession();
		session.setAttribute("password", "zxlww");
		//�洢һ��cookie
		Cookie cookie=new Cookie("loginname", "lixiao");
		cookie.setMaxAge(7200);
		resp.addCookie(cookie);
		resp.sendRedirect("index.jsp");
	}
}

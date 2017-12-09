package com.weixin.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/three")
public class ThreeServlet extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//获取cookie
		System.out.println("========================================三次传递======================================");
		Cookie[] cookies=req.getCookies();
		for (Cookie cookie : cookies) {
			if(cookie.getName().equals("loginname")){
				System.out.println("登录名："+cookie.getValue());
			}
			if(cookie.getName().equals("JSESSIONID")){
				String sessionid=cookie.getValue();
				System.out.println("sessionid:"+sessionid);
			}
		}
		HttpSession session=req.getSession();
		String name=(String) session.getAttribute("password");
		System.out.println("session保存的值："+name);
	}
}

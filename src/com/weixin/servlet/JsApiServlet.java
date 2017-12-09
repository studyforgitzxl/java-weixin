package com.weixin.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import com.weixin.utils.JSApiUtils;

@WebServlet("/config/jsapi")
public class JsApiServlet extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.sendRedirect("../wxlogin.jsp");
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setHeader("Access-Control-Allow-Origin", "*");
		Map<String,Object> info=JSApiUtils.getSignaure();
		JSONObject jsonObj=JSONObject.fromObject(info);
		String str=jsonObj.toString();
		System.out.println(str);
		PrintWriter out=resp.getWriter();
		out.print(str);
	}
}

package com.weixin.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.weixin.utils.AuthUtils;

@WebServlet("/wxLogin")
public class LoginServlet extends HttpServlet{
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String redirect_uri="http://f5616918.ngrok.io/java-weixin/callBack";
		String url="https://open.weixin.qq.com/connect/oauth2/authorize?appid="+AuthUtils.APPID
				+ "&redirect_uri="+redirect_uri
				+ "&response_type=code"
				+ "&scope=snsapi_userinfo"
				+ "&state=STATE#wechat_redirect";
		resp.sendRedirect(url);
	}
	
}

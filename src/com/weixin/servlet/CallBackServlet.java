package com.weixin.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import com.weixin.utils.AuthUtils;

@WebServlet("/callBack")
public class CallBackServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String code=req.getParameter("code");
		String url="https://api.weixin.qq.com/sns/oauth2/access_token?appid="+AuthUtils.APPID
				+ "&secret="+AuthUtils.APPSECRET
				+ "&code="+code
				+ "&grant_type=authorization_code";
		JSONObject jsonObject=AuthUtils.doGet(url);
		String accessToken=jsonObject.getString("access_token");
		String openid=jsonObject.getString("openid");
		String getinfourl="https://api.weixin.qq.com/sns/userinfo?access_token="+accessToken
				+ "&openid="+openid
				+ "&lang=zh_CN";
		JSONObject userInfo=AuthUtils.doGet(getinfourl);
		System.out.println(userInfo);
	}
}

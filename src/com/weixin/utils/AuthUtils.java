package com.weixin.utils;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import net.sf.json.JSONObject;

@SuppressWarnings("deprecation")
public class AuthUtils {
	
	public static final String APPID="wx5782d440eb6a1f0d";
	public static final String APPSECRET="8a64f749122a592ea9647b9b059f5e09";
	
	/**
	 * 发送一个get请求
	 * @param url
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static JSONObject doGet(String url) throws ClientProtocolException, IOException{
		JSONObject jsonObject=null;
		@SuppressWarnings("resource")
		DefaultHttpClient client=new DefaultHttpClient();
		HttpGet httpGet=new HttpGet(url);
		HttpResponse response=client.execute(httpGet);
		HttpEntity entity=response.getEntity();
		if(entity != null){
			String result=EntityUtils.toString(entity,"UTF-8");
			jsonObject=JSONObject.fromObject(result); 
		}
		httpGet.releaseConnection();
		return jsonObject;
	}
	
}

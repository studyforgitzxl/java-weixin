package com.weixin.utils;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.ClientProtocolException;
import com.bstek.tools.Md5Utils;

import net.sf.json.JSONObject;

public class JSApiUtils {
	
	/**
	 * 获取JS-SDK 的jsapi_ticket
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static String getJsapiTicket() throws ClientProtocolException, IOException{
		//首先获取access_token
		String accessToken=accessToken();
		System.out.println("accesstoken："+accessToken);
		String url="https://api.weixin.qq.com/cgi-bin/ticket/getticket?"
				+ "access_token="+accessToken
				+ "&type=jsapi";
		JSONObject jsonObject=AuthUtils.doGet(url);
		String jsapiTiket=jsonObject.getString("ticket");
		return jsapiTiket;
	}
	
	
	public static String accessToken() throws ClientProtocolException, IOException{
		String url="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential"
				+ "&appid="+AuthUtils.APPID
				+ "&secret="+AuthUtils.APPSECRET;
		JSONObject jsonObject=AuthUtils.doGet(url);
		String accessToken=jsonObject.getString("access_token");
		return accessToken;
	}
	
	
	public static Map<String,Object> getSignaure() throws ClientProtocolException, IOException{
		Map<String,Object> map=new HashMap<String, Object>();
		
		String noncestr="Wm3WZYTPz0wzccnW";
		String jsapi_ticket=getJsapiTicket();
		String timestamp=System.currentTimeMillis()+"";
		timestamp=timestamp.substring(0,10);
		String url="http://192.168.1.107:8080/java-weixin/wxlogin.jsp";
		
		//字典序列排序  
        Map<String,String> paraMap = new HashMap<String,String>();  
        paraMap.put("noncestr",noncestr);  
        paraMap.put("jsapi_ticket",jsapi_ticket );  
        paraMap.put("timestamp", timestamp);  
        paraMap.put("url",url);  
        String url1 = formatUrlMap(paraMap, false, true);
        String signature=new SHA2().getDigestOfString(url1.getBytes());

        map.put("signature", signature);
        map.put("noncestr", noncestr);
		map.put("jsapi_ticket", jsapi_ticket);
		map.put("timestamp", timestamp);
		map.put("url", url);
		return map;
	}
	
		/** 
	    *  
	    * 方法用途: 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序），并且生成url参数串<br> 
	    * 实现步骤: <br> 
	    *  
	    * @param paraMap   要排序的Map对象 
	    * @param urlEncode   是否需要URLENCODE 
	    * @param keyToLower    是否需要将Key转换为全小写 
	    *            true:key转化成小写，false:不转化 
	    * @return 
	    */  
	   public static String formatUrlMap(Map<String, String> paraMap, boolean urlEncode, boolean keyToLower)  
	   {  
	       String buff = "";  
	       Map<String, String> tmpMap = paraMap;  
	       try  
	       {  
	           List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(tmpMap.entrySet());  
	           // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）  
	           Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>()  
	           {  
	  
	               @Override  
	               public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2)  
	               {  
	                   return (o1.getKey()).toString().compareTo(o2.getKey());  
	               }  
	           });  
	           // 构造URL 键值对的格式  
	           StringBuilder buf = new StringBuilder();  
	           for (Map.Entry<String, String> item : infoIds)  
	           {  
	               if (StringUtils.isNotBlank(item.getKey()))  
	               {  
	                   String key = item.getKey();  
	                   String val = item.getValue();  
	                   if (urlEncode)  
	                   {  
	                       val = URLEncoder.encode(val, "utf-8");  
	                   }  
	                   if (keyToLower)  
	                   {  
	                       buf.append(key.toLowerCase() + "=" + val);  
	                   } else  
	                   {  
	                       buf.append(key + "=" + val);  
	                   }  
	                   buf.append("&");  
	               }  
	  
	           }  
	           buff = buf.toString();  
	           if (buff.isEmpty() == false)  
	           {  
	               buff = buff.substring(0, buff.length() - 1);  
	           }  
	       } catch (Exception e)  
	       {  
	          return null;  
	       }  
	       return buff;  
	   }  
	   
	   /**
	    * 统一下单
	    * @return
	    */
	   public Map<String,String> order(){
		   
		   return null;
	   }
	   
	   /**
	    * map转成xml
	    * @param map
	    * @return
	    */
	   public static String map2str(Map<String, String> map){  
	        String xmlStr = null;  
	        StringBuffer sbf = new StringBuffer();  
	        sbf.append("<xml>");  
	        for(Entry<String, String> s: map.entrySet()){  
	              
	            sbf.append("<")  
	                .append(s.getKey())  
	                .append(">")  
	                .append(s.getValue())  
	                .append("</")  
	                .append(s.getKey())  
	                .append(">");  
	                  
	        }  
	        sbf.append("</xml>");  
	        xmlStr = sbf.toString();  
	        return xmlStr;  
	    }  
	   
	 
}

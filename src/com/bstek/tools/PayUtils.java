package com.bstek.tools;

import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.weixin.utils.HttpRequest;

public class PayUtils {
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
	   
	 //xml形式的字符串转换为map集合  
	    public static Map<String,Object> xmlStr2Map(String xmlStr){  
	        Map<String,Object> map = new HashMap<String,Object>();  
	        Document doc;  
	        try {  
	            doc = DocumentHelper.parseText(xmlStr);  
	            Element root = doc.getRootElement();    
	            List children = root.elements();    
	            if(children != null && children.size() > 0) {    
	                for(int i = 0; i < children.size(); i++) {    
	                    Element child = (Element)children.get(i);    
	                    map.put(child.getName(), child.getTextTrim());    
	                }    
	            }    
	        } catch (DocumentException e) {  
	            e.printStackTrace();  
	        }  
	        return map;  
	    }
	    
	    /**
	     * 生成支付签名
	     * @param map
	     * @param key为商户平台设置的密钥key
	     * @return
	     */
	    public static String createSign(Map<String,String> map,String key){
	    	String stringA=formatUrlMap(map, false, false);
	    	System.out.println("stringA："+stringA);
	    	String stringSignTemp=stringA+"&key="+key; //注：key为商户平台设置的密钥key
	    	System.out.println("stringSignTemp："+stringSignTemp);
	    	//MD5签名方式
	    	String sign=Md5Utils.stringMD5(stringSignTemp);
	    	return sign.toUpperCase();
	    }
	    
	    public static void main(String[] args) {
//			Map<String,String> map=new HashMap<String, String>();
//			String key="m127n728fh6dd2cb65b6q16868687878";
//			map.put("appid", "wx5782d440eb6a1f0d");
//			map.put("mch_id", "1464026402");
//			map.put("nonce_str", System.currentTimeMillis()/1000+"");
//			map.put("body", "Java公众号支付测试");
//			map.put("out_trade_no", System.currentTimeMillis()*10000+"key");
//			map.put("total_fee", "1");
//			map.put("spbill_create_ip", "127.0.0.1");
//			map.put("notify_url", "http://zhuge.xnit.net/a/HomePage/home/service");
//			map.put("trade_type", "JSAPI");
//			map.put("openid", "omZ4Tv7IOzgaOjgQCI13sq8qT3ak");
//			String sign=createSign(map,key);
//			System.out.println("sign:"+sign);
//			map.put("sign", sign);
//			System.out.println(map);
//			
//			String xmlstr="<xml><sign>D1798C25681F234406822D88C9C34F30</sign><body>Java公众号支付测试</body><mch_id>1464026402</mch_id><spbill_create_ip>127.0.0.1</spbill_create_ip><total_fee>1</total_fee><notify_url>http://zhuge.xnit.net/a/HomePage/home/service</notify_url><appid>wx5782d440eb6a1f0d</appid><openid>omZ4Tv7IOzgaOjgQCI13sq8qT3ak</openid><out_trade_no>15126269564450000key</out_trade_no><nonce_str>1512626956</nonce_str><trade_type>JSAPI</trade_type></xml>";
//			System.out.println(xmlstr);
//			String createOrderURL = "https://api.mch.weixin.qq.com/pay/unifiedorder";  
//			String json=HttpRequest.httpPost2(createOrderURL, xmlstr);
//			System.out.println(json);
	    	
	    	Map<String,String> map2=new HashMap<String, String>();
	    	map2.put("appId", "wx5782d440eb6a1f0d");
	    	map2.put("timeStamp", System.currentTimeMillis()/1000+"");
	    	map2.put("nonceStr", System.currentTimeMillis()/10+"key");
	    	map2.put("package", "prepay_id=wx201712071421262797cf9bcd0902705152");
	    	map2.put("signType", "MD5");
	    	String sign=createSign(map2,"m127n728fh6dd2cb65b6q16868687878");
	    	System.out.println(sign);
	    	map2.put("paySign", "94D9C53E54A38AE4F3013CB146E80C56");
		}
	    
}

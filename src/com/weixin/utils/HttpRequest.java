package com.weixin.utils;

import java.io.IOException;
import java.net.URLDecoder;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
public class HttpRequest {
    private static Logger logger = LoggerFactory.getLogger(HttpRequest.class);    //��־��¼
 
    /**
     * httpPost
     * @param url  ·��
     * @param jsonParam ����
     * @return
     */
    public static JSONObject httpPost(String url,String jsonParam){
        return httpPost(url, jsonParam, false);
    }
    
    public static String httpPost2(String url,String jsonParam){
        return httpPost2(url, jsonParam, false);
    }
 
    /**
     * post����
     * @param url         url��ַ
     * @param jsonParam     ����
     * @param noNeedResponse    ����Ҫ���ؽ��
     * @return
     */
    public static JSONObject httpPost(String url,String jsonParam, boolean noNeedResponse){
        //post���󷵻ؽ��
        DefaultHttpClient httpClient = new DefaultHttpClient();
        JSONObject jsonResult = null;
        HttpPost method = new HttpPost(url);
        try {
            if (null != jsonParam) {
                //���������������
                StringEntity entity = new StringEntity(jsonParam.toString(), "utf-8");
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/json");
                method.setEntity(entity);
            }
            HttpResponse result = httpClient.execute(method);
            url = URLDecoder.decode(url, "UTF-8");
            /**�����ͳɹ������õ���Ӧ**/
            if (result.getStatusLine().getStatusCode() == 200) {
                String str = "";
                try {
                    /**��ȡ���������ع�����json�ַ�������**/
                    str = EntityUtils.toString(result.getEntity(),"UTF-8");
                    if (noNeedResponse) {
                        return null;
                    }
                    /**��json�ַ���ת����json����**/
                    jsonResult = JSONObject.fromObject(str);
                } catch (Exception e) {
                    logger.error("post�����ύʧ��:" + url, e);
                }
            }
        } catch (IOException e) {
            logger.error("post�����ύʧ��:" + url, e);
        }
        return jsonResult;
    }
    
    /**
     * post����
     * @param url         url��ַ
     * @param jsonParam     ����
     * @param noNeedResponse    ����Ҫ���ؽ��
     * @return
     */
    public static String httpPost2(String url,String jsonParam, boolean noNeedResponse){
        //post���󷵻ؽ��
        DefaultHttpClient httpClient = new DefaultHttpClient();
        String jsonResult = null;
        HttpPost method = new HttpPost(url);
        try {
            if (null != jsonParam) {
                //���������������
                StringEntity entity = new StringEntity(jsonParam.toString(), "utf-8");
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/json");
                method.setEntity(entity);
            }
            HttpResponse result = httpClient.execute(method);
            url = URLDecoder.decode(url, "UTF-8");
            /**�����ͳɹ������õ���Ӧ**/
            if (result.getStatusLine().getStatusCode() == 200) {
                String str = "";
                try {
                    /**��ȡ���������ع�����json�ַ�������**/
                    str = EntityUtils.toString(result.getEntity(),"UTF-8");
                    if (noNeedResponse) {
                        return null;
                    }
                    jsonResult=str;
                } catch (Exception e) {
                    logger.error("post�����ύʧ��:" + url, e);
                }
            }
        } catch (IOException e) {
            logger.error("post�����ύʧ��:" + url, e);
        }
        return jsonResult;
    }
 
 
    /**
     * ����get����
     * @param url    ·��
     * @return
     */
    public static JSONObject httpGet(String url){
        //get���󷵻ؽ��
        JSONObject jsonResult = null;
        try {
            DefaultHttpClient client = new DefaultHttpClient();
            //����get����
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);
 
            /**�����ͳɹ������õ���Ӧ**/
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                /**��ȡ���������ع�����json�ַ�������**/
                String strResult = EntityUtils.toString(response.getEntity(),"UTF-8");
                
                /**��json�ַ���ת����json����**/
                jsonResult = JSONObject.fromObject(strResult);
                url = URLDecoder.decode(url, "UTF-8");
            } else {
                logger.error("get�����ύʧ��:" + url);
            }
        } catch (IOException e) {
            logger.error("get�����ύʧ��:" + url, e);
        }
        return jsonResult;
    }
    
    public static void main(String[] args) {
    	JSONObject s=HttpRequest.httpGet("http://www.gps902.net/api/GetTracking.aspx?id=356803210126875&mapType=baidu&key=20161222HLXTJDMW730XY");
        System.out.println(s);
	}
}
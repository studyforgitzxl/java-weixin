package com.weixin.utils;

import java.util.Random;

/**
 * ���ں�֧��
 * @author xnit
 *
 */
public class PayUtils {
	//����
	public static final  String APPID="wx5782d440eb6a1f0d";//appid
	public static final int MCH_ID=1464026402;//�̻�id
	public static final String notify_url="";//֪ͨ��ַ
	
	public void pay(){
		String nonce_str=getRandomStr(30);//����ַ���
		String sign="";//ǩ��
		String sign_type="MD5";
		String body="΢��֧������";
		String out_trade_no=getRandomStr(20);//�̻�������
		String total_fee="1";//��۽��
		String spbill_create_ip="";//�ն�ip
		String trade_type="JSAPI";//��������
	}
	
	public static String getRandomStr(int length){
		//����30λ��������ַ���
		String base = "abcdefghijklmnopqrstuvwxyz0123456789";     
	    Random random = new Random();     
	    StringBuffer sb = new StringBuffer();     
	    for (int i = 0; i < length; i++) {     
	        int number = random.nextInt(base.length());     
	        sb.append(base.charAt(number));     
	    }     
	    return sb.toString();  
	}
	
	public static void main(String[] args) {
		System.out.println(getRandomStr(30));
	}
}

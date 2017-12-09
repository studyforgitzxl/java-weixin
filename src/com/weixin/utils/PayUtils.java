package com.weixin.utils;

import java.util.Random;

/**
 * 公众号支付
 * @author xnit
 *
 */
public class PayUtils {
	//参数
	public static final  String APPID="wx5782d440eb6a1f0d";//appid
	public static final int MCH_ID=1464026402;//商户id
	public static final String notify_url="";//通知地址
	
	public void pay(){
		String nonce_str=getRandomStr(30);//随机字符串
		String sign="";//签名
		String sign_type="MD5";
		String body="微信支付调起";
		String out_trade_no=getRandomStr(20);//商户订单号
		String total_fee="1";//标价金额
		String spbill_create_ip="";//终端ip
		String trade_type="JSAPI";//交易类型
	}
	
	public static String getRandomStr(int length){
		//生成30位以内随机字符串
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

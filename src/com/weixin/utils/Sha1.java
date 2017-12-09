package com.weixin.utils;

import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Sha1 {  
      
    /** 
     * SHA1 ��ȫ�����㷨 
     * @param maps ����key-value map���� 
     * @return 
     * @throws DigestException  
     */  
    public static String SHA1(Map<String,Object> maps) throws DigestException {  
        //��ȡ��ϢժҪ - �����ֵ�������ַ���  
        String decrypt = getOrderByLexicographic(maps);  
        try {  
            //ָ��sha1�㷨  
            MessageDigest digest = MessageDigest.getInstance("SHA-1");  
            digest.update(decrypt.getBytes());  
            //��ȡ�ֽ�����  
            byte messageDigest[] = digest.digest();  
            // Create Hex String  
            StringBuffer hexString = new StringBuffer();  
            // �ֽ�����ת��Ϊ ʮ������ ��  
            for (int i = 0; i < messageDigest.length; i++) {  
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);  
                if (shaHex.length() < 2) {  
                    hexString.append(0);  
                }  
                hexString.append(shaHex);  
            }  
            return hexString.toString().toUpperCase();  
  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
            throw new DigestException("ǩ������");  
        }  
    }  
    /** 
     * ��ȡ�������ֵ����� 
     * @param maps ����key-value map���� 
     * @return String �������ַ��� 
     */  
    private static String getOrderByLexicographic(Map<String,Object> maps){  
        return splitParams(lexicographicOrder(getParamsName(maps)),maps);  
    }  
    /** 
     * ��ȡ�������� key 
     * @param maps ����key-value map���� 
     * @return 
     */  
    private static List<String> getParamsName(Map<String,Object> maps){  
        List<String> paramNames = new ArrayList<String>();  
        for(Map.Entry<String,Object> entry : maps.entrySet()){  
            paramNames.add(entry.getKey());  
        }  
        return paramNames;  
    }  
    /** 
     * �������ư��ֵ����� 
     * @param paramNames ��������List���� 
     * @return �����Ĳ�������List���� 
     */  
    private static List<String> lexicographicOrder(List<String> paramNames){  
        Collections.sort(paramNames);  
        return paramNames;  
    }  
    /** 
     * ƴ������õĲ������ƺͲ���ֵ 
     * @param paramNames �����Ĳ������Ƽ��� 
     * @param maps ����key-value map���� 
     * @return String ƴ�Ӻ���ַ��� 
     */  
    private static String splitParams(List<String> paramNames,Map<String,Object> maps){  
        StringBuilder paramStr = new StringBuilder();  
        for(String paramName : paramNames){  
            paramStr.append(paramName);  
            for(Map.Entry<String,Object> entry : maps.entrySet()){  
                if(paramName.equals(entry.getKey())){  
                    paramStr.append(String.valueOf(entry.getValue()));  
                }  
            }  
        }  
        return paramStr.toString();  
    }
}
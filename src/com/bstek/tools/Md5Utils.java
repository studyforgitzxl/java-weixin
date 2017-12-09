package com.bstek.tools;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Md5
 * �ṩ���ַ�����md5-->stringMD5 
 * �ṩ���ļ���Md5-->fileMD5  
 * 
 * ���ڴ��ļ�������ʹ��DigestInputStream
 */
public class Md5Utils {

    protected static char hexDigits[] = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};  
    protected static MessageDigest messageDigest = null;
    
    static{  
        try{  
            // �õ�һ��MD5ת�����������ҪSHA1�������ɡ�SHA1����
            messageDigest = MessageDigest.getInstance("MD5");  
        }catch (NoSuchAlgorithmException e) {  
            System.err.println(Md5Utils.class.getName()+"��ʼ��ʧ�ܣ�MessageDigest��֧��MD5Util.");  
            e.printStackTrace();  
        }  
    }
    
    private static String bufferToHex(byte bytes[], int m, int n) {  
       StringBuffer stringbuffer = new StringBuffer(2 * n);  
       int k = m + n;  
       for (int l = m; l < k; l++) {  
        appendHexPair(bytes[l], stringbuffer);  
       }  
       return stringbuffer.toString();  
    }  
          
    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {  
     
       char c0 = hexDigits[(bt & 0xf0) >> 4];  
       char c1 = hexDigits[bt & 0xf];  
       stringbuffer.append(c0);  
       stringbuffer.append(c1);  
    } 
    
    private static String bufferToHex(byte bytes[]) {  
        return bufferToHex(bytes, 0, bytes.length);  
    }
    
    /**
     * �ַ�����md5����
     * @param input
     * @return
     */
    public static String stringMD5(String input) {
          // ������ַ���ת�����ֽ�����
         byte[] inputByteArray = input.getBytes();
         // inputByteArray�������ַ���ת���õ����ֽ�����
         messageDigest.update(inputByteArray);
         // ת�������ؽ����Ҳ���ֽ����飬����16��Ԫ��
         byte[] resultByteArray = messageDigest.digest();
         // �ַ�����ת�����ַ�������
         return bufferToHex(resultByteArray);
    }
    /**
     * �ļ���md5����
     * @param inputFile
     * @return
     * @throws IOException
     */
    public static String fileMD5(String inputFile) throws IOException {
          // ��������С��������Գ��һ��������
          int bufferSize = 256 * 1024;
          FileInputStream fileInputStream = null;
          DigestInputStream digestInputStream = null;
          try {
             // ʹ��DigestInputStream
             fileInputStream = new FileInputStream(inputFile);
             digestInputStream = new DigestInputStream(fileInputStream,messageDigest);
             // read�Ĺ����н���MD5����ֱ�������ļ�
             byte[] buffer =new byte[bufferSize];
             while (digestInputStream.read(buffer) > 0);
             // ��ȡ���յ�MessageDigest
             messageDigest= digestInputStream.getMessageDigest();
             // �õ������Ҳ���ֽ����飬����16��Ԫ��
             byte[] resultByteArray = messageDigest.digest();
             // ͬ�������ֽ�����ת�����ַ���
             return bufferToHex(resultByteArray);
          } finally {
             try {
                digestInputStream.close();
             } catch (Exception e) {
             }
             try {
                fileInputStream.close();
             } catch (Exception e) {
             }
          }
       }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        //�����ַ���MD5����
        //123456: e10adc3949ba59abbe56e057f20f883e
        //eastcom:  6997c46956185a7c4d452646fc9c69e2
        System.out.println(stringMD5("eastcom"));
        
        
        try {
            long startTime = System.currentTimeMillis();
            //�����ļ�MD5����
            String FilePath = "D:/ilink_ide.zip"; //4227e9fc4bd71ff34887d47867967b29
            System.out.println(fileMD5(FilePath));
            
            long endTime = System.currentTimeMillis(); 
            System.out.println((endTime - startTime)/1000); 
            
        } catch (IOException e) {
            
            e.printStackTrace();
        }
    }

    /**
     * �ο����£�
     *  http://www.zhihu.com/question/23702510
     *  http://blog.csdn.net/lf_software_studio/article/details/8025497
     *  http://my.oschina.net/laigous/blog/106646
     *  http://blog.csdn.net/wangqiuyun/article/details/22941433
     */

    
    
}
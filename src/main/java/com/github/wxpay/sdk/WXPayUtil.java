package main.java.com.github.wxpay.sdk;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.*;
import java.security.MessageDigest;

import main.java.com.github.wxpay.sdk.WXPayConstants.SignType;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WXPayUtil {

    /**
     * XML鏍煎紡瀛楃涓茶浆鎹负Map
     *
     * @param strXML XML瀛楃涓�
     * @return XML鏁版嵁杞崲鍚庣殑Map
     * @throws Exception
     */
    public static Map<String, String> xmlToMap(String strXML) throws Exception {
        try {
            Map<String, String> data = new HashMap<String, String>();
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            InputStream stream = new ByteArrayInputStream(strXML.getBytes("UTF-8"));
            org.w3c.dom.Document doc = documentBuilder.parse(stream);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getDocumentElement().getChildNodes();
            for (int idx = 0; idx < nodeList.getLength(); ++idx) {
                Node node = nodeList.item(idx);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                    data.put(element.getNodeName(), element.getTextContent());
                }
            }
            try {
                stream.close();
            } catch (Exception ex) {
                // do nothing
            }
            return data;
        } catch (Exception ex) {
            WXPayUtil.getLogger().warn("Invalid XML, can not convert to map. Error message: {}. XML content: {}", ex.getMessage(), strXML);
            throw ex;
        }

    }

    /**
     * 灏哅ap杞崲涓篨ML鏍煎紡鐨勫瓧绗︿覆
     *
     * @param data Map绫诲瀷鏁版嵁
     * @return XML鏍煎紡鐨勫瓧绗︿覆
     * @throws Exception
     */
    public static String mapToXml(Map<String, String> data) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder= documentBuilderFactory.newDocumentBuilder();
        org.w3c.dom.Document document = documentBuilder.newDocument();
        org.w3c.dom.Element root = document.createElement("xml");
        document.appendChild(root);
        for (String key: data.keySet()) {
            String value = data.get(key);
            if (value == null) {
                value = "";
            }
            value = value.trim();
            org.w3c.dom.Element filed = document.createElement(key);
            filed.appendChild(document.createTextNode(value));
            root.appendChild(filed);
        }
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        DOMSource source = new DOMSource(document);
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);
        String output = writer.getBuffer().toString(); //.replaceAll("\n|\r", "");
        try {
            writer.close();
        }
        catch (Exception ex) {
        }
        return output;
    }


    /**
     * 鐢熸垚甯︽湁 sign 鐨�XML 鏍煎紡瀛楃涓�
     *
     * @param data Map绫诲瀷鏁版嵁
     * @param key API瀵嗛挜
     * @return 鍚湁sign瀛楁鐨刋ML
     */
    public static String generateSignedXml(final Map<String, String> data, String key) throws Exception {
        return generateSignedXml(data, key, SignType.MD5);
    }

    /**
     * 鐢熸垚甯︽湁 sign 鐨�XML 鏍煎紡瀛楃涓�
     *
     * @param data Map绫诲瀷鏁版嵁
     * @param key API瀵嗛挜
     * @param signType 绛惧悕绫诲瀷
     * @return 鍚湁sign瀛楁鐨刋ML
     */
    public static String generateSignedXml(final Map<String, String> data, String key, SignType signType) throws Exception {
        String sign = generateSignature(data, key, signType);
        data.put(WXPayConstants.FIELD_SIGN, sign);
        return mapToXml(data);
    }


    /**
     * 鍒ゆ柇绛惧悕鏄惁姝ｇ‘
     *
     * @param xmlStr XML鏍煎紡鏁版嵁
     * @param key API瀵嗛挜
     * @return 绛惧悕鏄惁姝ｇ‘
     * @throws Exception
     */
    public static boolean isSignatureValid(String xmlStr, String key) throws Exception {
        Map<String, String> data = xmlToMap(xmlStr);
        if (!data.containsKey(WXPayConstants.FIELD_SIGN) ) {
            return false;
        }
        String sign = data.get(WXPayConstants.FIELD_SIGN);
        return generateSignature(data, key).equals(sign);
    }

    /**
     * 鍒ゆ柇绛惧悕鏄惁姝ｇ‘锛屽繀椤诲寘鍚玸ign瀛楁锛屽惁鍒欒繑鍥瀎alse銆備娇鐢∕D5绛惧悕銆�
     *
     * @param data Map绫诲瀷鏁版嵁
     * @param key API瀵嗛挜
     * @return 绛惧悕鏄惁姝ｇ‘
     * @throws Exception
     */
    public static boolean isSignatureValid(Map<String, String> data, String key) throws Exception {
        return isSignatureValid(data, key, SignType.MD5);
    }

    /**
     * 鍒ゆ柇绛惧悕鏄惁姝ｇ‘锛屽繀椤诲寘鍚玸ign瀛楁锛屽惁鍒欒繑鍥瀎alse銆�
     *
     * @param data Map绫诲瀷鏁版嵁
     * @param key API瀵嗛挜
     * @param signType 绛惧悕鏂瑰紡
     * @return 绛惧悕鏄惁姝ｇ‘
     * @throws Exception
     */
    public static boolean isSignatureValid(Map<String, String> data, String key, SignType signType) throws Exception {
        if (!data.containsKey(WXPayConstants.FIELD_SIGN) ) {
            return false;
        }
        String sign = data.get(WXPayConstants.FIELD_SIGN);
        return generateSignature(data, key, signType).equals(sign);
    }

    /**
     * 鐢熸垚绛惧悕
     *
     * @param data 寰呯鍚嶆暟鎹�
     * @param key API瀵嗛挜
     * @return 绛惧悕
     */
    public static String generateSignature(final Map<String, String> data, String key) throws Exception {
        return generateSignature(data, key, SignType.MD5);
    }

    /**
     * 鐢熸垚绛惧悕. 娉ㄦ剰锛岃嫢鍚湁sign_type瀛楁锛屽繀椤诲拰signType鍙傛暟淇濇寔涓�嚧銆�
     *
     * @param data 寰呯鍚嶆暟鎹�
     * @param key API瀵嗛挜
     * @param signType 绛惧悕鏂瑰紡
     * @return 绛惧悕
     */
    public static String generateSignature(final Map<String, String> data, String key, SignType signType) throws Exception {
        Set<String> keySet = data.keySet();
        String[] keyArray = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        for (String k : keyArray) {
            if (k.equals(WXPayConstants.FIELD_SIGN)) {
                continue;
            }
            if (data.get(k).trim().length() > 0) // 鍙傛暟鍊间负绌猴紝鍒欎笉鍙備笌绛惧悕
                sb.append(k).append("=").append(data.get(k).trim()).append("&");
        }
        sb.append("key=").append(key);
        if (SignType.MD5.equals(signType)) {
            return MD5(sb.toString()).toUpperCase();
        }
        else if (SignType.HMACSHA256.equals(signType)) {
            return HMACSHA256(sb.toString(), key);
        }
        else {
            throw new Exception(String.format("Invalid sign_type: %s", signType));
        }
    }


    /**
     * 鑾峰彇闅忔満瀛楃涓�Nonce Str
     *
     * @return String 闅忔満瀛楃涓�
     */
    public static String generateNonceStr() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 32);
    }


    /**
     * 鐢熸垚 MD5
     *
     * @param data 寰呭鐞嗘暟鎹�
     * @return MD5缁撴灉
     */
    public static String MD5(String data) throws Exception {
        java.security.MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] array = md.digest(data.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte item : array) {
            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString().toUpperCase();
    }

    /**
     * 鐢熸垚 HMACSHA256
     * @param data 寰呭鐞嗘暟鎹�
     * @param key 瀵嗛挜
     * @return 鍔犲瘑缁撴灉
     * @throws Exception
     */
    public static String HMACSHA256(String data, String key) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] array = sha256_HMAC.doFinal(data.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte item : array) {
            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString().toUpperCase();
    }

    /**
     * 鏃ュ織
     * @return
     */
    public static Logger getLogger() {
        Logger logger = LoggerFactory.getLogger("wxpay java sdk");
        return logger;
    }

    /**
     * 鑾峰彇褰撳墠鏃堕棿鎴筹紝鍗曚綅绉�
     * @return
     */
    public static long getCurrentTimestamp() {
        return System.currentTimeMillis()/1000;
    }

    /**
     * 鑾峰彇褰撳墠鏃堕棿鎴筹紝鍗曚綅姣
     * @return
     */
    public static long getCurrentTimestampMs() {
        return System.currentTimeMillis();
    }

    /**
     * 鐢熸垚 uuid锛�鍗崇敤鏉ユ爣璇嗕竴绗斿崟锛屼篃鐢ㄥ仛 nonce_str
     * @return
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 32);
    }

}

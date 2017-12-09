package main.java.com.github.wxpay.sdk;

import java.util.HashMap;
import java.util.Map;

import main.java.com.github.wxpay.sdk.WXPayConstants.SignType;

public class WXPay {

    private WXPayConfig config;
    private SignType signType;
    private boolean autoReport;
    private boolean useSandbox;
    private String notifyUrl;
    private WXPayRequest wxPayRequest;

    public WXPay(final WXPayConfig config) throws Exception {
        this(config, null, true, false);
    }

    public WXPay(final WXPayConfig config, final boolean autoReport) throws Exception {
        this(config, null, autoReport, false);
    }


    public WXPay(final WXPayConfig config, final boolean autoReport, final boolean useSandbox) throws Exception{
        this(config, null, autoReport, useSandbox);
    }

    public WXPay(final WXPayConfig config, final String notifyUrl) throws Exception {
        this(config, notifyUrl, true, false);
    }

    public WXPay(final WXPayConfig config, final String notifyUrl, final boolean autoReport) throws Exception {
        this(config, notifyUrl, autoReport, false);
    }

    public WXPay(final WXPayConfig config, final String notifyUrl, final boolean autoReport, final boolean useSandbox) throws Exception {
        this.config = config;
        this.notifyUrl = notifyUrl;
        this.autoReport = autoReport;
        this.useSandbox = useSandbox;
        if (useSandbox) {
            this.signType = SignType.MD5; // 娌欑鐜
        }
        else {
            this.signType = SignType.HMACSHA256;
        }
        this.wxPayRequest = new WXPayRequest(config);
    }

    private void checkWXPayConfig() throws Exception {
        if (this.config == null) {
            throw new Exception("config is null");
        }
        if (this.config.getAppID() == null || this.config.getAppID().trim().length() == 0) {
            throw new Exception("appid in config is empty");
        }
        if (this.config.getMchID() == null || this.config.getMchID().trim().length() == 0) {
            throw new Exception("appid in config is empty");
        }
        if (this.config.getCertStream() == null) {
            throw new Exception("cert stream in config is empty");
        }
//        if (this.config.getPrimaryDomain() == null || this.config.getPrimaryDomain().trim().length() == 0) {
//            throw new Exception("primary domain in config is empty");
//        }
//
//        // todo 娴峰灏卞～涓や釜鐩稿悓鐨勶紵 涓嬮潰鐨勯�杈戝緟鑰冭檻
//        if (this.config.getAlternateDomain() == null || this.config.getAlternateDomain().trim().length() == 0) {
//            throw new Exception("alternate domain in config is empty");
//        }
        if (this.config.getWXPayDomain() == null){
            throw new Exception("config.getWXPayDomain() is null");
        }

        if (this.config.getHttpConnectTimeoutMs() < 10) {
            throw new Exception("http connect timeout is too small");
        }
        if (this.config.getHttpReadTimeoutMs() < 10) {
            throw new Exception("http read timeout is too small");
        }

    }

    /**
     * 鍚�Map 涓坊鍔�appid銆乵ch_id銆乶once_str銆乻ign_type銆乻ign <br>
     * 璇ュ嚱鏁伴�鐢ㄤ簬鍟嗘埛閫傜敤浜庣粺涓�笅鍗曠瓑鎺ュ彛锛屼笉閫傜敤浜庣孩鍖呫�浠ｉ噾鍒告帴鍙�
     *
     * @param reqData
     * @return
     * @throws Exception
     */
    public Map<String, String> fillRequestData(Map<String, String> reqData) throws Exception {
        reqData.put("appid", config.getAppID());
        reqData.put("mch_id", config.getMchID());
        reqData.put("nonce_str", WXPayUtil.generateUUID());
        if (SignType.MD5.equals(this.signType)) {
            reqData.put("sign_type", WXPayConstants.MD5);
        }
        else if (SignType.HMACSHA256.equals(this.signType)) {
            reqData.put("sign_type", WXPayConstants.HMACSHA256);
        }
        reqData.put("sign", WXPayUtil.generateSignature(reqData, config.getKey(), this.signType));
        return reqData;
    }

    /**
     * 鍒ゆ柇xml鏁版嵁鐨剆ign鏄惁鏈夋晥锛屽繀椤诲寘鍚玸ign瀛楁锛屽惁鍒欒繑鍥瀎alse銆�
     *
     * @param reqData 鍚憌xpay post鐨勮姹傛暟鎹�
     * @return 绛惧悕鏄惁鏈夋晥
     * @throws Exception
     */
    public boolean isResponseSignatureValid(Map<String, String> reqData) throws Exception {
        // 杩斿洖鏁版嵁鐨勭鍚嶆柟寮忓拰璇锋眰涓粰瀹氱殑绛惧悕鏂瑰紡鏄竴鑷寸殑
        return WXPayUtil.isSignatureValid(reqData, this.config.getKey(), this.signType);
    }

    /**
     * 鍒ゆ柇鏀粯缁撴灉閫氱煡涓殑sign鏄惁鏈夋晥
     *
     * @param reqData 鍚憌xpay post鐨勮姹傛暟鎹�
     * @return 绛惧悕鏄惁鏈夋晥
     * @throws Exception
     */
    public boolean isPayResultNotifySignatureValid(Map<String, String> reqData) throws Exception {
        String signTypeInData = reqData.get(WXPayConstants.FIELD_SIGN_TYPE);
        SignType signType;
        if (signTypeInData == null) {
            signType = SignType.MD5;
        }
        else {
            signTypeInData = signTypeInData.trim();
            if (signTypeInData.length() == 0) {
                signType = SignType.MD5;
            }
            else if (WXPayConstants.MD5.equals(signTypeInData)) {
                signType = SignType.MD5;
            }
            else if (WXPayConstants.HMACSHA256.equals(signTypeInData)) {
                signType = SignType.HMACSHA256;
            }
            else {
                throw new Exception(String.format("Unsupported sign_type: %s", signTypeInData));
            }
        }
        return WXPayUtil.isSignatureValid(reqData, this.config.getKey(), signType);
    }


    /**
     * 涓嶉渶瑕佽瘉涔︾殑璇锋眰
     * @param urlSuffix String
     * @param reqData 鍚憌xpay post鐨勮姹傛暟鎹�
     * @param connectTimeoutMs 瓒呮椂鏃堕棿锛屽崟浣嶆槸姣
     * @param readTimeoutMs 瓒呮椂鏃堕棿锛屽崟浣嶆槸姣
     * @return API杩斿洖鏁版嵁
     * @throws Exception
     */
    public String requestWithoutCert(String urlSuffix, Map<String, String> reqData,
                                     int connectTimeoutMs, int readTimeoutMs) throws Exception {
        String msgUUID = reqData.get("nonce_str");
        String reqBody = WXPayUtil.mapToXml(reqData);

        String resp = this.wxPayRequest.requestWithoutCert(urlSuffix, msgUUID, reqBody, connectTimeoutMs, readTimeoutMs, autoReport);
        return resp;
    }


    /**
     * 闇�璇佷功鐨勮姹�
     * @param urlSuffix String
     * @param reqData 鍚憌xpay post鐨勮姹傛暟鎹� Map
     * @param connectTimeoutMs 瓒呮椂鏃堕棿锛屽崟浣嶆槸姣
     * @param readTimeoutMs 瓒呮椂鏃堕棿锛屽崟浣嶆槸姣
     * @return API杩斿洖鏁版嵁
     * @throws Exception
     */
    public String requestWithCert(String urlSuffix, Map<String, String> reqData,
                                  int connectTimeoutMs, int readTimeoutMs) throws Exception {
        String msgUUID= reqData.get("nonce_str");
        String reqBody = WXPayUtil.mapToXml(reqData);

        String resp = this.wxPayRequest.requestWithCert(urlSuffix, msgUUID, reqBody, connectTimeoutMs, readTimeoutMs, this.autoReport);
        return resp;
    }

    /**
     * 澶勭悊 HTTPS API杩斿洖鏁版嵁锛岃浆鎹㈡垚Map瀵硅薄銆俽eturn_code涓篠UCCESS鏃讹紝楠岃瘉绛惧悕銆�
     * @param xmlStr API杩斿洖鐨刋ML鏍煎紡鏁版嵁
     * @return Map绫诲瀷鏁版嵁
     * @throws Exception
     */
    public Map<String, String> processResponseXml(String xmlStr) throws Exception {
        String RETURN_CODE = "return_code";
        String return_code;
        Map<String, String> respData = WXPayUtil.xmlToMap(xmlStr);
        if (respData.containsKey(RETURN_CODE)) {
            return_code = respData.get(RETURN_CODE);
        }
        else {
            throw new Exception(String.format("No `return_code` in XML: %s", xmlStr));
        }

        if (return_code.equals(WXPayConstants.FAIL)) {
            return respData;
        }
        else if (return_code.equals(WXPayConstants.SUCCESS)) {
           if (this.isResponseSignatureValid(respData)) {
               return respData;
           }
           else {
               throw new Exception(String.format("Invalid sign value in XML: %s", xmlStr));
           }
        }
        else {
            throw new Exception(String.format("return_code value %s is invalid in XML: %s", return_code, xmlStr));
        }
    }

    /**
     * 浣滅敤锛氭彁浜ゅ埛鍗℃敮浠�br>
     * 鍦烘櫙锛氬埛鍗℃敮浠�
     * @param reqData 鍚憌xpay post鐨勮姹傛暟鎹�
     * @return API杩斿洖鏁版嵁
     * @throws Exception
     */
    public Map<String, String> microPay(Map<String, String> reqData) throws Exception {
        return this.microPay(reqData, this.config.getHttpConnectTimeoutMs(), this.config.getHttpReadTimeoutMs());
    }


    /**
     * 浣滅敤锛氭彁浜ゅ埛鍗℃敮浠�br>
     * 鍦烘櫙锛氬埛鍗℃敮浠�
     * @param reqData 鍚憌xpay post鐨勮姹傛暟鎹�
     * @param connectTimeoutMs 杩炴帴瓒呮椂鏃堕棿锛屽崟浣嶆槸姣
     * @param readTimeoutMs 璇昏秴鏃舵椂闂达紝鍗曚綅鏄绉�
     * @return API杩斿洖鏁版嵁
     * @throws Exception
     */
    public Map<String, String> microPay(Map<String, String> reqData, int connectTimeoutMs, int readTimeoutMs) throws Exception {
        String url;
        if (this.useSandbox) {
            url = WXPayConstants.SANDBOX_MICROPAY_URL_SUFFIX;
        }
        else {
            url = WXPayConstants.MICROPAY_URL_SUFFIX;
        }
        String respXml = this.requestWithoutCert(url, this.fillRequestData(reqData), connectTimeoutMs, readTimeoutMs);
        return this.processResponseXml(respXml);
    }

    /**
     * 鎻愪氦鍒峰崱鏀粯锛岄拡瀵硅蒋POS锛屽敖鍙兘鍋氭垚鍔�
     * 鍐呯疆閲嶈瘯鏈哄埗锛屾渶澶�0s
     * @param reqData
     * @return
     * @throws Exception
     */
    public Map<String, String> microPayWithPos(Map<String, String> reqData) throws Exception {
        return this.microPayWithPos(reqData, this.config.getHttpConnectTimeoutMs());
    }

    /**
     * 鎻愪氦鍒峰崱鏀粯锛岄拡瀵硅蒋POS锛屽敖鍙兘鍋氭垚鍔�
     * 鍐呯疆閲嶈瘯鏈哄埗锛屾渶澶�0s
     * @param reqData
     * @param connectTimeoutMs
     * @return
     * @throws Exception
     */
    public Map<String, String> microPayWithPos(Map<String, String> reqData, int connectTimeoutMs) throws Exception {
        int remainingTimeMs = 60*1000;
        long startTimestampMs = 0;
        Map<String, String> lastResult = null;
        Exception lastException = null;

        while (true) {
            startTimestampMs = WXPayUtil.getCurrentTimestampMs();
            int readTimeoutMs = remainingTimeMs - connectTimeoutMs;
            if (readTimeoutMs > 1000) {
                try {
                    lastResult = this.microPay(reqData, connectTimeoutMs, readTimeoutMs);
                    String returnCode = lastResult.get("return_code");
                    if (returnCode.equals("SUCCESS")) {
                        String resultCode = lastResult.get("result_code");
                        String errCode = lastResult.get("err_code");
                        if (resultCode.equals("SUCCESS")) {
                            break;
                        }
                        else {
                            // 鐪嬮敊璇爜锛岃嫢鏀粯缁撴灉鏈煡锛屽垯閲嶈瘯鎻愪氦鍒峰崱鏀粯
                            if (errCode.equals("SYSTEMERROR") || errCode.equals("BANKERROR") || errCode.equals("USERPAYING")) {
                                remainingTimeMs = remainingTimeMs - (int)(WXPayUtil.getCurrentTimestampMs() - startTimestampMs);
                                if (remainingTimeMs <= 100) {
                                    break;
                                }
                                else {
                                    WXPayUtil.getLogger().info("microPayWithPos: try micropay again");
                                    if (remainingTimeMs > 5*1000) {
                                        Thread.sleep(5*1000);
                                    }
                                    else {
                                        Thread.sleep(1*1000);
                                    }
                                    continue;
                                }
                            }
                            else {
                                break;
                            }
                        }
                    }
                    else {
                        break;
                    }
                }
                catch (Exception ex) {
                    lastResult = null;
                    lastException = ex;
                }
            }
            else {
                break;
            }
        }

        if (lastResult == null) {
            throw lastException;
        }
        else {
            return lastResult;
        }
    }



    /**
     * 浣滅敤锛氱粺涓�笅鍗�br>
     * 鍦烘櫙锛氬叕鍏卞彿鏀粯銆佹壂鐮佹敮浠樸�APP鏀粯
     * @param reqData 鍚憌xpay post鐨勮姹傛暟鎹�
     * @return API杩斿洖鏁版嵁
     * @throws Exception
     */
    public Map<String, String> unifiedOrder(Map<String, String> reqData) throws Exception {
        return this.unifiedOrder(reqData, config.getHttpConnectTimeoutMs(), this.config.getHttpReadTimeoutMs());
    }


    /**
     * 浣滅敤锛氱粺涓�笅鍗�br>
     * 鍦烘櫙锛氬叕鍏卞彿鏀粯銆佹壂鐮佹敮浠樸�APP鏀粯
     * @param reqData 鍚憌xpay post鐨勮姹傛暟鎹�
     * @param connectTimeoutMs 杩炴帴瓒呮椂鏃堕棿锛屽崟浣嶆槸姣
     * @param readTimeoutMs 璇昏秴鏃舵椂闂达紝鍗曚綅鏄绉�
     * @return API杩斿洖鏁版嵁
     * @throws Exception
     */
    public Map<String, String> unifiedOrder(Map<String, String> reqData,  int connectTimeoutMs, int readTimeoutMs) throws Exception {
        String url;
        if (this.useSandbox) {
            url = WXPayConstants.SANDBOX_UNIFIEDORDER_URL_SUFFIX;
        }
        else {
            url = WXPayConstants.UNIFIEDORDER_URL_SUFFIX;
        }
        if(this.notifyUrl != null) {
            reqData.put("notify_url", this.notifyUrl);
        }
        String respXml = this.requestWithoutCert(url, this.fillRequestData(reqData), connectTimeoutMs, readTimeoutMs);
        return this.processResponseXml(respXml);
    }


    /**
     * 浣滅敤锛氭煡璇㈣鍗�br>
     * 鍦烘櫙锛氬埛鍗℃敮浠樸�鍏叡鍙锋敮浠樸�鎵爜鏀粯銆丄PP鏀粯
     * @param reqData 鍚憌xpay post鐨勮姹傛暟鎹�
     * @return API杩斿洖鏁版嵁
     * @throws Exception
     */
    public Map<String, String> orderQuery(Map<String, String> reqData) throws Exception {
        return this.orderQuery(reqData, config.getHttpConnectTimeoutMs(), this.config.getHttpReadTimeoutMs());
    }


    /**
     * 浣滅敤锛氭煡璇㈣鍗�br>
     * 鍦烘櫙锛氬埛鍗℃敮浠樸�鍏叡鍙锋敮浠樸�鎵爜鏀粯銆丄PP鏀粯
     * @param reqData 鍚憌xpay post鐨勮姹傛暟鎹�int
     * @param connectTimeoutMs 杩炴帴瓒呮椂鏃堕棿锛屽崟浣嶆槸姣
     * @param readTimeoutMs 璇昏秴鏃舵椂闂达紝鍗曚綅鏄绉�
     * @return API杩斿洖鏁版嵁
     * @throws Exception
     */
    public Map<String, String> orderQuery(Map<String, String> reqData, int connectTimeoutMs, int readTimeoutMs) throws Exception {
        String url;
        if (this.useSandbox) {
            url = WXPayConstants.SANDBOX_ORDERQUERY_URL_SUFFIX;
        }
        else {
            url = WXPayConstants.ORDERQUERY_URL_SUFFIX;
        }
        String respXml = this.requestWithoutCert(url, this.fillRequestData(reqData), connectTimeoutMs, readTimeoutMs);
        return this.processResponseXml(respXml);
    }


    /**
     * 浣滅敤锛氭挙閿�鍗�br>
     * 鍦烘櫙锛氬埛鍗℃敮浠�
     * @param reqData 鍚憌xpay post鐨勮姹傛暟鎹�
     * @return API杩斿洖鏁版嵁
     * @throws Exception
     */
    public Map<String, String> reverse(Map<String, String> reqData) throws Exception {
        return this.reverse(reqData, config.getHttpConnectTimeoutMs(), this.config.getHttpReadTimeoutMs());
    }


    /**
     * 浣滅敤锛氭挙閿�鍗�br>
     * 鍦烘櫙锛氬埛鍗℃敮浠�br>
     * 鍏朵粬锛氶渶瑕佽瘉涔�
     * @param reqData 鍚憌xpay post鐨勮姹傛暟鎹�
     * @param connectTimeoutMs 杩炴帴瓒呮椂鏃堕棿锛屽崟浣嶆槸姣
     * @param readTimeoutMs 璇昏秴鏃舵椂闂达紝鍗曚綅鏄绉�
     * @return API杩斿洖鏁版嵁
     * @throws Exception
     */
    public Map<String, String> reverse(Map<String, String> reqData, int connectTimeoutMs, int readTimeoutMs) throws Exception {
        String url;
        if (this.useSandbox) {
            url = WXPayConstants.SANDBOX_REVERSE_URL_SUFFIX;
        }
        else {
            url = WXPayConstants.REVERSE_URL_SUFFIX;
        }
        String respXml = this.requestWithCert(url, this.fillRequestData(reqData), connectTimeoutMs, readTimeoutMs);
        return this.processResponseXml(respXml);
    }


    /**
     * 浣滅敤锛氬叧闂鍗�br>
     * 鍦烘櫙锛氬叕鍏卞彿鏀粯銆佹壂鐮佹敮浠樸�APP鏀粯
     * @param reqData 鍚憌xpay post鐨勮姹傛暟鎹�
     * @return API杩斿洖鏁版嵁
     * @throws Exception
     */
    public Map<String, String> closeOrder(Map<String, String> reqData) throws Exception {
        return this.closeOrder(reqData, config.getHttpConnectTimeoutMs(), this.config.getHttpReadTimeoutMs());
    }


    /**
     * 浣滅敤锛氬叧闂鍗�br>
     * 鍦烘櫙锛氬叕鍏卞彿鏀粯銆佹壂鐮佹敮浠樸�APP鏀粯
     * @param reqData 鍚憌xpay post鐨勮姹傛暟鎹�
     * @param connectTimeoutMs 杩炴帴瓒呮椂鏃堕棿锛屽崟浣嶆槸姣
     * @param readTimeoutMs 璇昏秴鏃舵椂闂达紝鍗曚綅鏄绉�
     * @return API杩斿洖鏁版嵁
     * @throws Exception
     */
    public Map<String, String> closeOrder(Map<String, String> reqData,  int connectTimeoutMs, int readTimeoutMs) throws Exception {
        String url;
        if (this.useSandbox) {
            url = WXPayConstants.SANDBOX_CLOSEORDER_URL_SUFFIX;
        }
        else {
            url = WXPayConstants.CLOSEORDER_URL_SUFFIX;
        }
        String respXml = this.requestWithoutCert(url, this.fillRequestData(reqData), connectTimeoutMs, readTimeoutMs);
        return this.processResponseXml(respXml);
    }


    /**
     * 浣滅敤锛氱敵璇烽�娆�br>
     * 鍦烘櫙锛氬埛鍗℃敮浠樸�鍏叡鍙锋敮浠樸�鎵爜鏀粯銆丄PP鏀粯
     * @param reqData 鍚憌xpay post鐨勮姹傛暟鎹�
     * @return API杩斿洖鏁版嵁
     * @throws Exception
     */
    public Map<String, String> refund(Map<String, String> reqData) throws Exception {
        return this.refund(reqData, this.config.getHttpConnectTimeoutMs(), this.config.getHttpReadTimeoutMs());
    }


    /**
     * 浣滅敤锛氱敵璇烽�娆�br>
     * 鍦烘櫙锛氬埛鍗℃敮浠樸�鍏叡鍙锋敮浠樸�鎵爜鏀粯銆丄PP鏀粯<br>
     * 鍏朵粬锛氶渶瑕佽瘉涔�
     * @param reqData 鍚憌xpay post鐨勮姹傛暟鎹�
     * @param connectTimeoutMs 杩炴帴瓒呮椂鏃堕棿锛屽崟浣嶆槸姣
     * @param readTimeoutMs 璇昏秴鏃舵椂闂达紝鍗曚綅鏄绉�
     * @return API杩斿洖鏁版嵁
     * @throws Exception
     */
    public Map<String, String> refund(Map<String, String> reqData, int connectTimeoutMs, int readTimeoutMs) throws Exception {
        String url;
        if (this.useSandbox) {
            url = WXPayConstants.SANDBOX_REFUND_URL_SUFFIX;
        }
        else {
            url = WXPayConstants.REFUND_URL_SUFFIX;
        }
        String respXml = this.requestWithCert(url, this.fillRequestData(reqData), connectTimeoutMs, readTimeoutMs);
        return this.processResponseXml(respXml);
    }


    /**
     * 浣滅敤锛氶�娆炬煡璇�br>
     * 鍦烘櫙锛氬埛鍗℃敮浠樸�鍏叡鍙锋敮浠樸�鎵爜鏀粯銆丄PP鏀粯
     * @param reqData 鍚憌xpay post鐨勮姹傛暟鎹�
     * @return API杩斿洖鏁版嵁
     * @throws Exception
     */
    public Map<String, String> refundQuery(Map<String, String> reqData) throws Exception {
        return this.refundQuery(reqData, this.config.getHttpConnectTimeoutMs(), this.config.getHttpReadTimeoutMs());
    }


    /**
     * 浣滅敤锛氶�娆炬煡璇�br>
     * 鍦烘櫙锛氬埛鍗℃敮浠樸�鍏叡鍙锋敮浠樸�鎵爜鏀粯銆丄PP鏀粯
     * @param reqData 鍚憌xpay post鐨勮姹傛暟鎹�
     * @param connectTimeoutMs 杩炴帴瓒呮椂鏃堕棿锛屽崟浣嶆槸姣
     * @param readTimeoutMs 璇昏秴鏃舵椂闂达紝鍗曚綅鏄绉�
     * @return API杩斿洖鏁版嵁
     * @throws Exception
     */
    public Map<String, String> refundQuery(Map<String, String> reqData, int connectTimeoutMs, int readTimeoutMs) throws Exception {
        String url;
        if (this.useSandbox) {
            url = WXPayConstants.SANDBOX_REFUNDQUERY_URL_SUFFIX;
        }
        else {
            url = WXPayConstants.REFUNDQUERY_URL_SUFFIX;
        }
        String respXml = this.requestWithoutCert(url, this.fillRequestData(reqData), connectTimeoutMs, readTimeoutMs);
        return this.processResponseXml(respXml);
    }


    /**
     * 浣滅敤锛氬璐﹀崟涓嬭浇锛堟垚鍔熸椂杩斿洖瀵硅处鍗曟暟鎹紝澶辫触鏃惰繑鍥瀀ML鏍煎紡鏁版嵁锛�br>
     * 鍦烘櫙锛氬埛鍗℃敮浠樸�鍏叡鍙锋敮浠樸�鎵爜鏀粯銆丄PP鏀粯
     * @param reqData 鍚憌xpay post鐨勮姹傛暟鎹�
     * @return API杩斿洖鏁版嵁
     * @throws Exception
     */
    public Map<String, String> downloadBill(Map<String, String> reqData) throws Exception {
        return this.downloadBill(reqData, this.config.getHttpConnectTimeoutMs(), this.config.getHttpReadTimeoutMs());
    }


    /**
     * 浣滅敤锛氬璐﹀崟涓嬭浇<br>
     * 鍦烘櫙锛氬埛鍗℃敮浠樸�鍏叡鍙锋敮浠樸�鎵爜鏀粯銆丄PP鏀粯<br>
     * 鍏朵粬锛氭棤璁烘槸鍚︽垚鍔熼兘杩斿洖Map銆傝嫢鎴愬姛锛岃繑鍥炵殑Map涓惈鏈塺eturn_code銆乺eturn_msg銆乨ata锛�
     *      鍏朵腑return_code涓篳SUCCESS`锛宒ata涓哄璐﹀崟鏁版嵁銆�
     * @param reqData 鍚憌xpay post鐨勮姹傛暟鎹�
     * @param connectTimeoutMs 杩炴帴瓒呮椂鏃堕棿锛屽崟浣嶆槸姣
     * @param readTimeoutMs 璇昏秴鏃舵椂闂达紝鍗曚綅鏄绉�
     * @return 缁忚繃灏佽鐨凙PI杩斿洖鏁版嵁
     * @throws Exception
     */
    public Map<String, String> downloadBill(Map<String, String> reqData, int connectTimeoutMs, int readTimeoutMs) throws Exception {
        String url;
        if (this.useSandbox) {
            url = WXPayConstants.SANDBOX_DOWNLOADBILL_URL_SUFFIX;
        }
        else {
            url = WXPayConstants.DOWNLOADBILL_URL_SUFFIX;
        }
        String respStr = this.requestWithoutCert(url, this.fillRequestData(reqData), connectTimeoutMs, readTimeoutMs).trim();
        Map<String, String> ret;
        // 鍑虹幇閿欒锛岃繑鍥瀀ML鏁版嵁
        if (respStr.indexOf("<") == 0) {
            ret = WXPayUtil.xmlToMap(respStr);
        }
        else {
            // 姝ｅ父杩斿洖csv鏁版嵁
            ret = new HashMap<String, String>();
            ret.put("return_code", WXPayConstants.SUCCESS);
            ret.put("return_msg", "ok");
            ret.put("data", respStr);
        }
        return ret;
    }


    /**
     * 浣滅敤锛氫氦鏄撲繚闅�br>
     * 鍦烘櫙锛氬埛鍗℃敮浠樸�鍏叡鍙锋敮浠樸�鎵爜鏀粯銆丄PP鏀粯
     * @param reqData 鍚憌xpay post鐨勮姹傛暟鎹�
     * @return API杩斿洖鏁版嵁
     * @throws Exception
     */
    public Map<String, String> report(Map<String, String> reqData) throws Exception {
        return this.report(reqData, this.config.getHttpConnectTimeoutMs(), this.config.getHttpReadTimeoutMs());
    }


    /**
     * 浣滅敤锛氫氦鏄撲繚闅�br>
     * 鍦烘櫙锛氬埛鍗℃敮浠樸�鍏叡鍙锋敮浠樸�鎵爜鏀粯銆丄PP鏀粯
     * @param reqData 鍚憌xpay post鐨勮姹傛暟鎹�
     * @param connectTimeoutMs 杩炴帴瓒呮椂鏃堕棿锛屽崟浣嶆槸姣
     * @param readTimeoutMs 璇昏秴鏃舵椂闂达紝鍗曚綅鏄绉�
     * @return API杩斿洖鏁版嵁
     * @throws Exception
     */
    public Map<String, String> report(Map<String, String> reqData, int connectTimeoutMs, int readTimeoutMs) throws Exception {
        String url;
        if (this.useSandbox) {
            url = WXPayConstants.SANDBOX_REPORT_URL_SUFFIX;
        }
        else {
            url = WXPayConstants.REPORT_URL_SUFFIX;
        }
        String respXml = this.requestWithoutCert(url, this.fillRequestData(reqData), connectTimeoutMs, readTimeoutMs);
        return WXPayUtil.xmlToMap(respXml);
    }


    /**
     * 浣滅敤锛氳浆鎹㈢煭閾炬帴<br>
     * 鍦烘櫙锛氬埛鍗℃敮浠樸�鎵爜鏀粯
     * @param reqData 鍚憌xpay post鐨勮姹傛暟鎹�
     * @return API杩斿洖鏁版嵁
     * @throws Exception
     */
    public Map<String, String> shortUrl(Map<String, String> reqData) throws Exception {
        return this.shortUrl(reqData, this.config.getHttpConnectTimeoutMs(), this.config.getHttpReadTimeoutMs());
    }


    /**
     * 浣滅敤锛氳浆鎹㈢煭閾炬帴<br>
     * 鍦烘櫙锛氬埛鍗℃敮浠樸�鎵爜鏀粯
     * @param reqData 鍚憌xpay post鐨勮姹傛暟鎹�
     * @return API杩斿洖鏁版嵁
     * @throws Exception
     */
    public Map<String, String> shortUrl(Map<String, String> reqData, int connectTimeoutMs, int readTimeoutMs) throws Exception {
        String url;
        if (this.useSandbox) {
            url = WXPayConstants.SANDBOX_SHORTURL_URL_SUFFIX;
        }
        else {
            url = WXPayConstants.SHORTURL_URL_SUFFIX;
        }
        String respXml = this.requestWithoutCert(url, this.fillRequestData(reqData), connectTimeoutMs, readTimeoutMs);
        return this.processResponseXml(respXml);
    }


    /**
     * 浣滅敤锛氭巿鏉冪爜鏌ヨOPENID鎺ュ彛<br>
     * 鍦烘櫙锛氬埛鍗℃敮浠�
     * @param reqData 鍚憌xpay post鐨勮姹傛暟鎹�
     * @return API杩斿洖鏁版嵁
     * @throws Exception
     */
    public Map<String, String> authCodeToOpenid(Map<String, String> reqData) throws Exception {
        return this.authCodeToOpenid(reqData, this.config.getHttpConnectTimeoutMs(), this.config.getHttpReadTimeoutMs());
    }


    /**
     * 浣滅敤锛氭巿鏉冪爜鏌ヨOPENID鎺ュ彛<br>
     * 鍦烘櫙锛氬埛鍗℃敮浠�
     * @param reqData 鍚憌xpay post鐨勮姹傛暟鎹�
     * @param connectTimeoutMs 杩炴帴瓒呮椂鏃堕棿锛屽崟浣嶆槸姣
     * @param readTimeoutMs 璇昏秴鏃舵椂闂达紝鍗曚綅鏄绉�
     * @return API杩斿洖鏁版嵁
     * @throws Exception
     */
    public Map<String, String> authCodeToOpenid(Map<String, String> reqData, int connectTimeoutMs, int readTimeoutMs) throws Exception {
        String url;
        if (this.useSandbox) {
            url = WXPayConstants.SANDBOX_AUTHCODETOOPENID_URL_SUFFIX;
        }
        else {
            url = WXPayConstants.AUTHCODETOOPENID_URL_SUFFIX;
        }
        String respXml = this.requestWithoutCert(url, this.fillRequestData(reqData), connectTimeoutMs, readTimeoutMs);
        return this.processResponseXml(respXml);
    }


} // end class

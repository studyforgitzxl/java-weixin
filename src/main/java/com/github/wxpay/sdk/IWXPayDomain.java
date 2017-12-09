package main.java.com.github.wxpay.sdk;

/**
 * 鍩熷悕绠＄悊锛屽疄鐜颁富澶囧煙鍚嶈嚜鍔ㄥ垏鎹�
 */
public abstract interface IWXPayDomain {
    /**
     * 涓婃姤鍩熷悕缃戠粶鐘跺喌
     * @param domain 鍩熷悕銆�姣斿锛歛pi.mch.weixin.qq.com
     * @param elapsedTimeMillis 鑰楁椂
     * @param ex 缃戠粶璇锋眰涓嚭鐜扮殑寮傚父銆�
     *           null琛ㄧず娌℃湁寮傚父
     *           ConnectTimeoutException锛岃〃绀哄缓绔嬬綉缁滆繛鎺ュ紓甯�
     *           UnknownHostException锛�琛ㄧずdns瑙ｆ瀽寮傚父
     */
    abstract void report(final String domain, long elapsedTimeMillis, final Exception ex);

    /**
     * 鑾峰彇鍩熷悕
     * @param config 閰嶇疆
     * @return 鍩熷悕
     */
    abstract DomainInfo getDomain(final WXPayConfig config);

    static class DomainInfo{
        public String domain;       //鍩熷悕
        public boolean primaryDomain;     //璇ュ煙鍚嶆槸鍚︿负涓诲煙鍚嶃�渚嬪:api.mch.weixin.qq.com涓轰富鍩熷悕
        public DomainInfo(String domain, boolean primaryDomain) {
            this.domain = domain;
            this.primaryDomain = primaryDomain;
        }

        @Override
        public String toString() {
            return "DomainInfo{" +
                    "domain='" + domain + '\'' +
                    ", primaryDomain=" + primaryDomain +
                    '}';
        }
    }

}
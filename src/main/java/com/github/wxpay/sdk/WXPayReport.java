package main.java.com.github.wxpay.sdk;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;

/**
 * 浜ゆ槗淇濋殰
 */
public class WXPayReport {

    public static class ReportInfo {

        /**
         * 甯冨皵鍙橀噺浣跨敤int銆�涓篺alse锛�1涓簍rue銆�
         */

        // 鍩烘湰淇℃伅
        private String version = "v0";
        private String sdk = "wxpay java sdk v1.0";
        private String uuid;  // 浜ゆ槗鐨勬爣璇�
        private long timestamp;   // 涓婃姤鏃剁殑鏃堕棿鎴筹紝鍗曚綅绉�
        private long elapsedTimeMillis; // 鑰楁椂锛屽崟浣�姣

        // 閽堝涓诲煙鍚�
        private String firstDomain;  // 绗�娆¤姹傜殑鍩熷悕
        private boolean primaryDomain; //鏄惁涓诲煙鍚�
        private int firstConnectTimeoutMillis;  // 绗�娆¤姹傝缃殑杩炴帴瓒呮椂鏃堕棿锛屽崟浣�姣
        private int firstReadTimeoutMillis;  // 绗�娆¤姹傝缃殑璇诲啓瓒呮椂鏃堕棿锛屽崟浣�姣
        private int firstHasDnsError;  // 绗�娆¤姹傛槸鍚﹀嚭鐜癲ns闂
        private int firstHasConnectTimeout; // 绗�娆¤姹傛槸鍚﹀嚭鐜拌繛鎺ヨ秴鏃�
        private int firstHasReadTimeout; // 绗�娆¤姹傛槸鍚﹀嚭鐜拌繛鎺ヨ秴鏃�

        public ReportInfo(String uuid, long timestamp, long elapsedTimeMillis, String firstDomain, boolean primaryDomain, int firstConnectTimeoutMillis, int firstReadTimeoutMillis, boolean firstHasDnsError, boolean firstHasConnectTimeout, boolean firstHasReadTimeout) {
            this.uuid = uuid;
            this.timestamp = timestamp;
            this.elapsedTimeMillis = elapsedTimeMillis;
            this.firstDomain = firstDomain;
            this.primaryDomain = primaryDomain;
            this.firstConnectTimeoutMillis = firstConnectTimeoutMillis;
            this.firstReadTimeoutMillis = firstReadTimeoutMillis;
            this.firstHasDnsError = firstHasDnsError?1:0;
            this.firstHasConnectTimeout = firstHasConnectTimeout?1:0;
            this.firstHasReadTimeout = firstHasReadTimeout?1:0;
         }

        @Override
        public String toString() {
            return "ReportInfo{" +
                    "version='" + version + '\'' +
                    ", sdk='" + sdk + '\'' +
                    ", uuid='" + uuid + '\'' +
                    ", timestamp=" + timestamp +
                    ", elapsedTimeMillis=" + elapsedTimeMillis +
                    ", firstDomain='" + firstDomain + '\'' +
                    ", primaryDomain=" + primaryDomain +
                    ", firstConnectTimeoutMillis=" + firstConnectTimeoutMillis +
                    ", firstReadTimeoutMillis=" + firstReadTimeoutMillis +
                    ", firstHasDnsError=" + firstHasDnsError +
                    ", firstHasConnectTimeout=" + firstHasConnectTimeout +
                    ", firstHasReadTimeout=" + firstHasReadTimeout +
                    '}';
        }

        /**
         * 杞崲鎴�csv 鏍煎紡
         *
         * @return
         */
        public String toLineString(String key) {
            String separator = ",";
            Object[] objects = new Object[] {
                version, sdk, uuid, timestamp, elapsedTimeMillis,
                    firstDomain, primaryDomain, firstConnectTimeoutMillis, firstReadTimeoutMillis,
                    firstHasDnsError, firstHasConnectTimeout, firstHasReadTimeout
            };
            StringBuffer sb = new StringBuffer();
            for(Object obj: objects) {
                sb.append(obj).append(separator);
            }
            try {
                String sign = WXPayUtil.HMACSHA256(sb.toString(), key);
                sb.append(sign);
                return sb.toString();
            }
            catch (Exception ex) {
                return null;
            }

        }

    }

    private static final String REPORT_URL = "http://report.mch.weixin.qq.com/wxpay/report/default";
    // private static final String REPORT_URL = "http://127.0.0.1:5000/test";


    private static final int DEFAULT_CONNECT_TIMEOUT_MS = 6*1000;
    private static final int DEFAULT_READ_TIMEOUT_MS = 8*1000;

    private LinkedBlockingQueue<String> reportMsgQueue = null;
    private WXPayConfig config;
    private ExecutorService executorService;

    private volatile static WXPayReport INSTANCE;

    private WXPayReport(final WXPayConfig config) {
        this.config = config;
        reportMsgQueue = new LinkedBlockingQueue<String>(config.getReportQueueMaxSize());

        // 娣诲姞澶勭悊绾跨▼
        executorService = Executors.newFixedThreadPool(config.getReportWorkerNum(), new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread t = Executors.defaultThreadFactory().newThread(r);
                t.setDaemon(true);
                return t;
            }
        });

        if (config.shouldAutoReport()) {
            WXPayUtil.getLogger().info("report worker num: {}", config.getReportWorkerNum());
            for (int i = 0; i < config.getReportWorkerNum(); ++i) {
                executorService.execute(new Runnable() {
                    public void run() {
                        while (true) {
                            // 鍏堢敤 take 鑾峰彇鏁版嵁
                            try {
                                StringBuffer sb = new StringBuffer();
                                String firstMsg = reportMsgQueue.take();
                                WXPayUtil.getLogger().info("get first report msg: {}", firstMsg);
                                String msg = null;
                                sb.append(firstMsg); //浼氶樆濉炶嚦鏈夋秷鎭�
                                int remainNum = config.getReportBatchSize() - 1;
                                for (int j=0; j<remainNum; ++j) {
                                    WXPayUtil.getLogger().info("try get remain report msg");
                                    // msg = reportMsgQueue.poll();  // 涓嶉樆濉炰簡
                                    msg = reportMsgQueue.take();
                                    WXPayUtil.getLogger().info("get remain report msg: {}", msg);
                                    if (msg == null) {
                                        break;
                                    }
                                    else {
                                        sb.append("\n");
                                        sb.append(msg);
                                    }
                                }
                                // 涓婃姤
                                WXPayReport.httpRequest(sb.toString(), DEFAULT_CONNECT_TIMEOUT_MS, DEFAULT_READ_TIMEOUT_MS);
                            }
                            catch (Exception ex) {
                                WXPayUtil.getLogger().warn("report fail. reason: {}", ex.getMessage());
                            }
                        }
                    }
                });
            }
        }

    }

    /**
     * 鍗曚緥锛屽弻閲嶆牎楠岋紝璇峰湪 JDK 1.5鍙婃洿楂樼増鏈腑浣跨敤
     *
     * @param config
     * @return
     */
    public static WXPayReport getInstance(WXPayConfig config) {
        if (INSTANCE == null) {
            synchronized (WXPayReport.class) {
                if (INSTANCE == null) {
                    INSTANCE = new WXPayReport(config);
                }
            }
        }
        return INSTANCE;
    }

    public void report(String uuid, long elapsedTimeMillis,
                       String firstDomain, boolean primaryDomain, int firstConnectTimeoutMillis, int firstReadTimeoutMillis,
                       boolean firstHasDnsError, boolean firstHasConnectTimeout, boolean firstHasReadTimeout) {
        long currentTimestamp = WXPayUtil.getCurrentTimestamp();
        ReportInfo reportInfo = new ReportInfo(uuid, currentTimestamp, elapsedTimeMillis,
                firstDomain, primaryDomain, firstConnectTimeoutMillis, firstReadTimeoutMillis,
                firstHasDnsError, firstHasConnectTimeout, firstHasReadTimeout);
        String data = reportInfo.toLineString(config.getKey());
        WXPayUtil.getLogger().info("report {}", data);
        if (data != null) {
            reportMsgQueue.offer(data);
        }
    }


    @Deprecated
    private void reportSync(final String data) throws Exception {
        httpRequest(data, DEFAULT_CONNECT_TIMEOUT_MS, DEFAULT_READ_TIMEOUT_MS);
    }

    @Deprecated
    private void reportAsync(final String data) throws Exception {
        new Thread(new Runnable() {
            public void run() {
                try {
                    httpRequest(data, DEFAULT_CONNECT_TIMEOUT_MS, DEFAULT_READ_TIMEOUT_MS);
                }
                catch (Exception ex) {
                    WXPayUtil.getLogger().warn("report fail. reason: {}", ex.getMessage());
                }
            }
        }).start();
    }

    /**
     * http 璇锋眰
     * @param data
     * @param connectTimeoutMs
     * @param readTimeoutMs
     * @return
     * @throws Exception
     */
    private static String httpRequest(String data, int connectTimeoutMs, int readTimeoutMs) throws Exception{
        BasicHttpClientConnectionManager connManager;
        connManager = new BasicHttpClientConnectionManager(
                RegistryBuilder.<ConnectionSocketFactory>create()
                        .register("http", PlainConnectionSocketFactory.getSocketFactory())
                        .register("https", SSLConnectionSocketFactory.getSocketFactory())
                        .build(),
                null,
                null,
                null
        );
        HttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(connManager)
                .build();

        HttpPost httpPost = new HttpPost(REPORT_URL);

        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(readTimeoutMs).setConnectTimeout(connectTimeoutMs).build();
        httpPost.setConfig(requestConfig);

        StringEntity postEntity = new StringEntity(data, "UTF-8");
        httpPost.addHeader("Content-Type", "text/xml");
        httpPost.addHeader("User-Agent", "wxpay sdk java v1.0 ");  // TODO: 寰堥噸瑕侊紝鐢ㄦ潵妫�祴 sdk 鐨勪娇鐢ㄦ儏鍐碉紝瑕佷笉瑕佸姞涓婂晢鎴蜂俊鎭紵
        httpPost.setEntity(postEntity);

        HttpResponse httpResponse = httpClient.execute(httpPost);
        HttpEntity httpEntity = httpResponse.getEntity();
        return EntityUtils.toString(httpEntity, "UTF-8");
    }

}

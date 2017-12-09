package main.java.com.github.wxpay.sdk;

import java.io.InputStream;

public abstract class WXPayConfig {



    /**
     * 鑾峰彇 App ID
     *
     * @return App ID
     */
    abstract String getAppID();


    /**
     * 鑾峰彇 Mch ID
     *
     * @return Mch ID
     */
    abstract String getMchID();


    /**
     * 鑾峰彇 API 瀵嗛挜
     *
     * @return API瀵嗛挜
     */
    abstract String getKey();


    /**
     * 鑾峰彇鍟嗘埛璇佷功鍐呭
     *
     * @return 鍟嗘埛璇佷功鍐呭
     */
    abstract InputStream getCertStream();

    /**
     * HTTP(S) 杩炴帴瓒呮椂鏃堕棿锛屽崟浣嶆绉�
     *
     * @return
     */
    public int getHttpConnectTimeoutMs() {
        return 6*1000;
    }

    /**
     * HTTP(S) 璇绘暟鎹秴鏃舵椂闂达紝鍗曚綅姣
     *
     * @return
     */
    public int getHttpReadTimeoutMs() {
        return 8*1000;
    }

    /**
     * 鑾峰彇WXPayDomain, 鐢ㄤ簬澶氬煙鍚嶅鐏捐嚜鍔ㄥ垏鎹�
     * @return
     */
    abstract IWXPayDomain getWXPayDomain();

    /**
     * 鏄惁鑷姩涓婃姤銆�
     * 鑻ヨ鍏抽棴鑷姩涓婃姤锛屽瓙绫讳腑瀹炵幇璇ュ嚱鏁拌繑鍥�false 鍗冲彲銆�
     *
     * @return
     */
    public boolean shouldAutoReport() {
        return true;
    }

    /**
     * 杩涜鍋ュ悍涓婃姤鐨勭嚎绋嬬殑鏁伴噺
     *
     * @return
     */
    public int getReportWorkerNum() {
        return 6;
    }


    /**
     * 鍋ュ悍涓婃姤缂撳瓨娑堟伅鐨勬渶澶ф暟閲忋�浼氭湁绾跨▼鍘荤嫭绔嬩笂鎶�
     * 绮楃暐璁＄畻锛氬姞鍏ヤ竴鏉℃秷鎭�00B锛�0000娑堟伅鍗犵敤绌洪棿 2000 KB锛岀害涓�MB锛屽彲浠ユ帴鍙�
     *
     * @return
     */
    public int getReportQueueMaxSize() {
        return 10000;
    }

    /**
     * 鎵归噺涓婃姤锛屼竴娆℃渶澶氫笂鎶ュ涓暟鎹�
     *
     * @return
     */
    public int getReportBatchSize() {
        return 10;
    }

}

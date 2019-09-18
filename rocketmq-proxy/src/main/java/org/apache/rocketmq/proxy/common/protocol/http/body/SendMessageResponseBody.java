package org.apache.rocketmq.proxy.common.protocol.http.body;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.rocketmq.proxy.common.ProtocolKey;
import org.apache.rocketmq.proxy.common.ProxyConstants;

public class SendMessageResponseBody extends Body {
    private Integer retCode;
    private String retMsg;
    private long resTime = System.currentTimeMillis();

    public Integer getRetCode() {
        return retCode;
    }

    public void setRetCode(Integer retCode) {
        this.retCode = retCode;
    }

    public String getRetMsg() {
        return retMsg;
    }

    public void setRetMsg(String retMsg) {
        this.retMsg = retMsg;
    }

    public long getResTime() {
        return resTime;
    }

    public void setResTime(long resTime) {
        this.resTime = resTime;
    }

    public static SendMessageResponseBody buildBody(Integer retCode, String retMsg) {
        SendMessageResponseBody sendMessageResponseBody = new SendMessageResponseBody();
        sendMessageResponseBody.setRetMsg(retMsg);
        sendMessageResponseBody.setResTime(System.currentTimeMillis());
        sendMessageResponseBody.setRetCode(retCode);
        return sendMessageResponseBody;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("sendMessageResponseBody={")
            .append("retCode=").append(retCode).append(",")
            .append("retMsg=").append(retMsg).append(",")
            .append("responseTime=").append(DateFormatUtils.format(resTime, ProxyConstants.DATE_FORMAT)).append("}");
        return sb.toString();
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(ProtocolKey.RETCODE, retCode);
        map.put(ProtocolKey.RETMSG, retMsg);
        map.put(ProtocolKey.RESTIME, resTime);
        return map;
    }
}
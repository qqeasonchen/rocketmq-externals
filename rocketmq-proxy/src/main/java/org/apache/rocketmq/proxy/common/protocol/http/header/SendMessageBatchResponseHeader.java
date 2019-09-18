package org.apache.rocketmq.proxy.common.protocol.http.header;

import java.util.HashMap;
import java.util.Map;
import org.apache.rocketmq.proxy.common.ProtocolKey;

public class SendMessageBatchResponseHeader extends Header {
    private int code;
    private String proxyCluster;
    private String proxyIp;
    private String proxyEnv;
    private String proxyRegion;
    private String proxyIdc;
    private String proxyDcn;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getProxyCluster() {
        return proxyCluster;
    }

    public void setProxyCluster(String proxyCluster) {
        this.proxyCluster = proxyCluster;
    }

    public String getProxyIp() {
        return proxyIp;
    }

    public void setProxyIp(String proxyIp) {
        this.proxyIp = proxyIp;
    }

    public String getProxyEnv() {
        return proxyEnv;
    }

    public void setProxyEnv(String proxyEnv) {
        this.proxyEnv = proxyEnv;
    }

    public String getProxyRegion() {
        return proxyRegion;
    }

    public void setProxyRegion(String proxyRegion) {
        this.proxyRegion = proxyRegion;
    }

    public String getProxyIdc() {
        return proxyIdc;
    }

    public void setProxyIdc(String proxyIdc) {
        this.proxyIdc = proxyIdc;
    }

    public String getProxyDcn() {
        return proxyDcn;
    }

    public void setProxyDcn(String proxyDcn) {
        this.proxyDcn = proxyDcn;
    }

    public static SendMessageBatchResponseHeader buildHeader(Integer requestCode, String proxyCluster,
        String proxyIp, String proxyEnv, String proxyRegion,
        String proxyDcn, String proxyIDC) {
        SendMessageBatchResponseHeader sendMessageBatchResponseHeader = new SendMessageBatchResponseHeader();
        sendMessageBatchResponseHeader.setCode(requestCode);
        sendMessageBatchResponseHeader.setProxyCluster(proxyCluster);
        sendMessageBatchResponseHeader.setProxyDcn(proxyDcn);
        sendMessageBatchResponseHeader.setProxyEnv(proxyEnv);
        sendMessageBatchResponseHeader.setProxyRegion(proxyRegion);
        sendMessageBatchResponseHeader.setProxyIdc(proxyIDC);
        sendMessageBatchResponseHeader.setProxyIp(proxyIp);
        return sendMessageBatchResponseHeader;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("sendMessageBatchResponseHeader={")
            .append("code=").append(code).append(",")
            .append("proxyEnv=").append(proxyEnv).append(",")
            .append("proxyRegion=").append(proxyRegion).append(",")
            .append("proxyIdc=").append(proxyIdc).append(",")
            .append("proxyDcn=").append(proxyDcn).append(",")
            .append("proxyCluster=").append(proxyCluster).append(",")
            .append("proxyIp=").append(proxyIp).append("}");
        return sb.toString();
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(ProtocolKey.REQUEST_CODE, code);
        map.put(ProtocolKey.ProxyInstanceKey.PROXYCLUSTER, proxyCluster);
        map.put(ProtocolKey.ProxyInstanceKey.PROXYIP, proxyIp);
        map.put(ProtocolKey.ProxyInstanceKey.PROXYENV, proxyEnv);
        map.put(ProtocolKey.ProxyInstanceKey.PROXYREGION, proxyRegion);
        map.put(ProtocolKey.ProxyInstanceKey.PROXYIDC, proxyIdc);
        map.put(ProtocolKey.ProxyInstanceKey.PROXYDCN, proxyDcn);
        return map;
    }
}

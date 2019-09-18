package org.apache.rocketmq.proxy.common;

public enum ProxyResponseCode {
    SUCCESS(0, "success"),
    OVERLOAD(1, "proxy overload, try later, "),
    PROXY_REQUESTCODE_INVALID(2, "requestCode can't be null, or must be number, "),
    PROXY_SEND_SYNC_MSG_ERR(3, "proxy send rr msg err, "),
    PROXY_WAITING_RR_MSG_ERR(4, "proxy waiting rr msg err, "),
    PROXY_SYSTEM_BUSY_ERROR(5, "proxy system busy, "),
    PROXY_PROTOCOL_HEADER_ERR(6, "proxy protocol[header] err, "),
    PROXY_PROTOCOL_BODY_ERR(7, "proxy protocol[body] err, "),
    PROXY_STOP(8, "proxy will stop or already stopped, "),
    PROXY_REJECT_BY_PROCESSOR_ERROR(9, "proxy reject by processor error, "),
    PROXY_BATCH_PRODUCER_STOPED_ERR(10, "proxy batch msg producer stopped, "),
    PROXY_BATCH_SPEED_OVER_LIMIT_ERR(11, "proxy batch msg speed over the limit, "),
    PROXY_PACKAGE_MSG_ERR(12, "proxy package msg err, "),
    PROXY_GROUP_PRODUCER_STOPED_ERR(13, "proxy group producer stopped, "),
    PROXY_SEND_ASYNC_MSG_ERR(14, "proxy send async msg err, "),
    PROXY_REPLY_MSG_ERR(15, "proxy reply msg err, "),
    PROXY_RUNTIME_ERR(16, "proxy runtime err, ");

    private Integer retCode;
    private String errMsg;

    private ProxyResponseCode(Integer retCode, String errMsg) {
        this.retCode = retCode;
        this.errMsg = errMsg;
    }

    public Integer getRetCode() {
        return this.retCode;
    }

    public void setRetCode(Integer retCode) {
        this.retCode = retCode;
    }

    public String getErrMsg() {
        return this.errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}

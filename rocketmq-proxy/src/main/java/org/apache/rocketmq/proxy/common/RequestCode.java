package org.apache.rocketmq.proxy.common;

public enum RequestCode {
    MSG_BATCH_SEND(102, "批量发送"),

    MSG_SEND(101, "单条发送"),

    MSG_TRACE_LOG(103, "日志上报"),

    HTTP_PUSH_CLIENT(103, "PUSH CLIENT BY HTTP POST"),

    REGISTER(201, "注册"),

    UNREGISTER(202, "去注册"),

    HEARTBEAT(203, "心跳, 发送方与消费方分别心跳, 类型区分"),

    SUBSCRIBE(206, "订阅"),

    UNSUBSCRIBE(207, "去订阅"),

    REPLY_MESSAGE(301, "发送返回消息"),

    ADMIN_METRICS(603, "管理接口, METRICS信息"),

    ADMIN_SHUTDOWN(601, "管理接口, SHUTDOWN");

    private Integer requestCode;

    private String desc;

    RequestCode(Integer requestCode, String desc) {
        this.requestCode = requestCode;
        this.desc = desc;
    }

    public static boolean contains(Integer requestCode) {
        boolean flag = false;
        for (RequestCode itr : RequestCode.values()) {
            if (itr.requestCode.intValue() == requestCode.intValue()) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    public static RequestCode get(Integer requestCode) {
        RequestCode ret = null;
        for (RequestCode itr : RequestCode.values()) {
            if (itr.requestCode.intValue() == requestCode.intValue()) {
                ret = itr;
                break;
            }
        }
        return ret;
    }

    public Integer getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(Integer requestCode) {
        this.requestCode = requestCode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

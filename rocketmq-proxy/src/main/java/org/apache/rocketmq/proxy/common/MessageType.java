package org.apache.rocketmq.proxy.common;

public enum MessageType {
    ASYNC(1, "async"),

    SYNC(2, "sync"),

    ONEWAY(3, "oneway");

    private Integer type;

    private String desc;

    MessageType(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static MessageType get(Integer type) {
        if (ASYNC.type.intValue() == type.intValue()) {
            return ASYNC;
        } else if (SYNC.type.intValue() == type.intValue()) {
            return SYNC;
        } else {
            return null;
        }
    }

    public static boolean contains(Integer messageType) {
        boolean flag = false;
        for (MessageType mt : MessageType.values()) {
            if (mt.type == messageType.intValue()) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

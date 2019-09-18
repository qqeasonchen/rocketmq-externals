package org.apache.rocketmq.proxy.core.protocol.http.producer;

import java.util.HashMap;
import java.util.Map;
import org.apache.rocketmq.common.message.Message;

public class SendMessageContext {
    private Message msg;

    private long createTime = System.currentTimeMillis();

    private Map<String, String> props;

    public SendMessageContext(Message msg) {
        this.msg = msg;
    }

    public void addProp(String key, String val) {
        if (props == null) {
            props = new HashMap<>();
        }
        props.put(key, val);
    }

    public String getProp(String key) {
        return props.get(key);
    }

    public Message getMsg() {
        return msg;
    }

    public void setMsg(Message msg) {
        this.msg = msg;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}

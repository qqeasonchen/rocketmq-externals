package org.apache.rocketmq.proxy.common.protocol.http.body;

import com.alibaba.fastjson.JSONObject;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.proxy.common.MessageType;

public class SendMessageRequestBody extends Body {
    public static final String TOPIC = "topic";
    public static final String MESSAGETYPE = "messageType";
    public static final String BIZSEQNO = "bizSeqNo";
    public static final String CONTENT = "content";
    public static final String TTL = "ttl";
    public static final String EXTFIELDS = "extfields";

    private String topic;
    private String messageType;
    private String bizSeqNo;
    private String ttl;
    private String content;
    private HashMap<String, String> extfields;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getBizSeqNo() {
        return bizSeqNo;
    }

    public void setBizSeqNo(String bizSeqNo) {
        this.bizSeqNo = bizSeqNo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public HashMap<String, String> getExtfields() {
        return extfields;
    }

    public void setExtfields(HashMap<String, String> extfields) {
        this.extfields = extfields;
    }

    public String getTtl() {
        return ttl;
    }

    public void setTtl(String ttl) {
        this.ttl = ttl;
    }

    public static SendMessageRequestBody buildBody(Map<String, Object> bodyParam) {
        SendMessageRequestBody body = new SendMessageRequestBody();
        body.setTopic(MapUtils.getString(bodyParam, TOPIC));
        String messageType = StringUtils.isBlank(MapUtils.getString(bodyParam, MESSAGETYPE))
            ? String.valueOf(MessageType.ASYNC.getType()) : MapUtils.getString(bodyParam, MESSAGETYPE);
        body.setMessageType(messageType);
        body.setBizSeqNo(MapUtils.getString(bodyParam, BIZSEQNO));
        body.setTtl(MapUtils.getString(bodyParam, TTL));
        body.setContent(MapUtils.getString(bodyParam, CONTENT));
        String extFields = MapUtils.getString(bodyParam, EXTFIELDS);
        if (StringUtils.isNotBlank(extFields)) {
            body.setExtfields(JSONObject.parseObject(extFields, HashMap.class));
        }
        return body;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(TOPIC, topic);
        map.put(MESSAGETYPE, messageType);
        map.put(BIZSEQNO, bizSeqNo);
        map.put(TTL, ttl);
        map.put(CONTENT, content);
        map.put(EXTFIELDS, extfields);
        return map;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("sendMessageRequestBody={")
            .append("topic=").append(topic).append(",")
            .append("messageType=").append(messageType).append(",")
            .append("bizSeqNo=").append(bizSeqNo).append(",")
            .append("content=").append(content).append(",")
            .append("ttl=").append(ttl).append(",")
            .append("extfields=").append(extfields).append("}");
        return sb.toString();
    }
}

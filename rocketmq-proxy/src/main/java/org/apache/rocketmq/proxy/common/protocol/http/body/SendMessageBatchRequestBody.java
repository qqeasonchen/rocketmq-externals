package org.apache.rocketmq.proxy.common.protocol.http.body;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

public class SendMessageBatchRequestBody extends Body {
    public static final String CONTENTS = "contents";
    public static final String TOPIC = "topic";

    private String topic;
    private List<BatchMessageEntity> contents;

    public SendMessageBatchRequestBody() {
    }

    public List<BatchMessageEntity> getContents() {
        return contents;
    }

    public void setContents(List<BatchMessageEntity> contents) {
        this.contents = contents;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("sendMessageBatchRequestBody={")
            .append("topic=").append(topic).append(",")
            .append("contents=").append(JSON.toJSONString(contents)).append("}");
        return sb.toString();
    }

    public static class BatchMessageEntity {
        public String bizSeqNo;
        public String topic;
        public String msg;
        public String tag;
        public String ttl;

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("batchMessageEntity={")
                .append("bizSeqNo=").append(bizSeqNo).append(",")
                .append("topic=").append(topic).append(",")
                .append("msg=").append(msg).append(",")
                .append("ttl=").append(ttl).append(",")
                .append("tag=").append(tag).append("}");
            return sb.toString();
        }
    }

    public static SendMessageBatchRequestBody buildBody(final Map<String, Object> bodyParam) throws Exception {
        String topic = MapUtils.getString(bodyParam, TOPIC, null);
        String contents = MapUtils.getString(bodyParam, CONTENTS, null);
        SendMessageBatchRequestBody body = new SendMessageBatchRequestBody();
        body.setTopic(topic);
        if (StringUtils.isNotBlank(contents)) {
            body.setContents(JSONArray.parseArray(contents, SendMessageBatchRequestBody.BatchMessageEntity.class));
        }
        return body;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(TOPIC, topic);
        map.put(CONTENTS, contents);
        return map;
    }

}

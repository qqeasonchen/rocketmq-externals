package org.apache.rocketmq.proxy.common.protocol.http.header;

import java.util.Map;
import org.apache.rocketmq.proxy.common.RequestCode;

public abstract class Header {
    public abstract Map<String, Object> toMap();

    public static Header buildHeader(String requestCode, Map<String, Object> originalMap) throws Exception {
        if (String.valueOf(RequestCode.MSG_BATCH_SEND.getRequestCode()).equals(requestCode)) {
            return SendMessageBatchRequestHeader.buildHeader(originalMap);
        } else if (String.valueOf(RequestCode.MSG_SEND.getRequestCode()).equals(requestCode)) {
            return SendMessageRequestHeader.buildHeader(originalMap);
        } else {
            throw new Exception();
        }
    }
}

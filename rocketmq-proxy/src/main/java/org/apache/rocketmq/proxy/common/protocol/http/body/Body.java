package org.apache.rocketmq.proxy.common.protocol.http.body;

import java.util.Map;
import org.apache.rocketmq.proxy.common.RequestCode;

public abstract class Body {

    public abstract Map<String, Object> toMap();

    public static Body buildBody(String requestCode, Map<String, Object> originalMap) throws Exception {
        if (String.valueOf(RequestCode.MSG_BATCH_SEND.getRequestCode()).equals(requestCode)) {
            return SendMessageBatchRequestBody.buildBody(originalMap);
        } else if (String.valueOf(RequestCode.MSG_SEND.getRequestCode()).equals(requestCode)) {
            return SendMessageRequestBody.buildBody(originalMap);
        } else {
            throw new Exception();
        }
    }
}

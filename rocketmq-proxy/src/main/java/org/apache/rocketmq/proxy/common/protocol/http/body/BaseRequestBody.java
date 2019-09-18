package org.apache.rocketmq.proxy.common.protocol.http.body;

import java.util.HashMap;
import java.util.Map;

public class BaseRequestBody extends Body {

    public static BaseRequestBody buildBody(Map<String, Object> bodyParam) {
        BaseRequestBody baseRequestBody = new BaseRequestBody();
        return baseRequestBody;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        return map;
    }
}
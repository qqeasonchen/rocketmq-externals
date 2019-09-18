package org.apache.rocketmq.proxy.common.protocol.http.header;

import java.util.HashMap;
import java.util.Map;
import org.apache.rocketmq.proxy.common.ProtocolKey;

public class BaseResponseHeader extends Header {

    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static BaseResponseHeader buildHeader(String code) {
        BaseResponseHeader baseResponseHeader = new BaseResponseHeader();
        baseResponseHeader.setCode(code);
        return baseResponseHeader;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(ProtocolKey.REQUEST_CODE, code);
        return map;
    }
}

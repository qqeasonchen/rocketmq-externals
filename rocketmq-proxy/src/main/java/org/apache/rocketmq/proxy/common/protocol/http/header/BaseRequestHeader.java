package org.apache.rocketmq.proxy.common.protocol.http.header;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections4.MapUtils;
import org.apache.rocketmq.proxy.common.ProtocolKey;

public class BaseRequestHeader extends Header {
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static BaseRequestHeader buildHeader(Map<String, Object> headerParam) {
        BaseRequestHeader header = new BaseRequestHeader();
        header.setCode(MapUtils.getString(headerParam, ProtocolKey.REQUEST_CODE));
        return header;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(ProtocolKey.REQUEST_CODE, code);
        return map;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("baseRequestHeader={code=")
            .append(code).append("}");
        return sb.toString();
    }
}

package org.apache.rocketmq.proxy.common.command;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.collections4.MapUtils;
import org.apache.rocketmq.proxy.common.ProxyConstants;
import org.apache.rocketmq.proxy.common.protocol.http.body.BaseResponseBody;
import org.apache.rocketmq.proxy.common.protocol.http.body.Body;
import org.apache.rocketmq.proxy.common.protocol.http.header.BaseResponseHeader;
import org.apache.rocketmq.proxy.common.protocol.http.header.Header;

public class HttpCommand {
    private static AtomicLong requestId = new AtomicLong(0);

    private long opaque;

    private String requestCode;

    public String httpMethod;

    public String httpVersion;

    public Header header;

    public Body body;

    public long requestTime;

    public long responseTime;

    public CmdType cmdType = CmdType.REQUEST;

    public HttpCommand(String httpMethod, String httpVersion, String requestCode) {
        this.httpMethod = httpMethod;
        this.httpVersion = httpVersion;
        this.requestTime = System.currentTimeMillis();
        this.requestCode = requestCode;
        this.opaque = requestId.incrementAndGet();
    }

    public HttpCommand createHttpCommandResponse(Header header,
        Body body) {
        HttpCommand response = new HttpCommand(this.httpMethod, this.httpVersion, this.requestCode);
        response.setOpaque(this.opaque);
        response.setRequestTime(this.requestTime);
        response.setHeader(header);
        response.setBody(body);
        response.setCmdType(CmdType.RESPONSE);
        response.setResponseTime(System.currentTimeMillis());
        return response;
    }

    public HttpCommand createHttpCommandResponse(Integer retCode, String retMsg) {
        HttpCommand response = new HttpCommand(this.httpMethod, this.httpVersion, this.requestCode);
        response.setOpaque(this.opaque);
        response.setRequestTime(this.requestTime);
        BaseResponseHeader baseResponseHeader = new BaseResponseHeader();
        baseResponseHeader.setCode(requestCode);
        response.setHeader(baseResponseHeader);
        BaseResponseBody baseResponseBody = new BaseResponseBody();
        baseResponseBody.setRetCode(retCode);
        baseResponseBody.setRetMsg(retMsg);
        response.setBody(baseResponseBody);
        response.setCmdType(CmdType.RESPONSE);
        response.setResponseTime(System.currentTimeMillis());
        return response;
    }

    public long getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(long requestTime) {
        this.requestTime = requestTime;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }

    public long getOpaque() {
        return opaque;
    }

    public void setOpaque(long opaque) {
        this.opaque = opaque;
    }

    public CmdType getCmdType() {
        return cmdType;
    }

    public void setCmdType(CmdType cmdType) {
        this.cmdType = cmdType;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }

    public String getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(String requestCode) {
        this.requestCode = requestCode;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("httpCommand={")
            .append(cmdType).append(",")
            .append(httpMethod).append("/").append(httpVersion).append(",")
            .append("requestCode=").append(requestCode).append(",")
            .append("opaque=").append(opaque).append(",");

        if (cmdType == CmdType.RESPONSE) {
            sb.append("cost=").append(responseTime - requestTime).append(",");
        }

        sb.append("header=").append(header).append(",")
            .append("body=").append(body)
            .append("}");

        return sb.toString();
    }

    public String abstractDesc() {
        StringBuilder sb = new StringBuilder();
        sb.append("httpCommand={")
            .append(cmdType).append(",")
            .append(httpMethod).append("/").append(httpVersion).append(",")
            .append("requestCode=").append(requestCode).append(",")
            .append("opaque=").append(opaque).append(",");

        if (cmdType == CmdType.RESPONSE) {
            sb.append("cost=").append(responseTime - requestTime).append(",");
        }

        sb.append("header=").append(header).append(",")
            .append("bodySize=").append(body.toString().length()).append("}");

        return sb.toString();
    }

    public String simpleDesc() {
        StringBuilder sb = new StringBuilder();
        sb.append("httpCommand={")
            .append(cmdType).append(",")
            .append(httpMethod).append("/").append(httpVersion).append(",")
            .append("requestCode=").append(requestCode).append("}");

        return sb.toString();
    }

    public DefaultFullHttpResponse httpResponse() throws Exception {
        if (cmdType == CmdType.REQUEST) {
            return null;
        }

        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
            HttpResponseStatus.OK,
            Unpooled.wrappedBuffer(JSON.toJSONString(this.getBody()).getBytes(ProxyConstants.DEFAULT_CHARSET)));
        response.headers().add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN +
            "; charset=" + ProxyConstants.DEFAULT_CHARSET);
        response.headers().add(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        response.headers().add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        Map<String, Object> customHeader = this.getHeader().toMap();
        if (MapUtils.isNotEmpty(customHeader)) {
            HttpHeaders heads = response.headers();
            for (String key : customHeader.keySet()) {
                heads.add(key, customHeader.get(key));
            }
        }
        return response;
    }

    public enum CmdType {
        REQUEST,
        RESPONSE
    }
}

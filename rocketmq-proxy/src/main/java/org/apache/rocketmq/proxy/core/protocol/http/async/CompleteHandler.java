package org.apache.rocketmq.proxy.core.protocol.http.async;

public interface CompleteHandler<T> {
    void onResponse(T t);
}
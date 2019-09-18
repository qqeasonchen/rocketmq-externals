package org.apache.rocketmq.proxy.core.protocol.http.async;

import com.google.common.base.Preconditions;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

public class AsyncContext<T> {

    private T request;

    private T response;

    private AtomicBoolean complete = new AtomicBoolean(Boolean.FALSE);

    private ThreadPoolExecutor asyncContextExecutor;

    public AsyncContext(T request, T response, ThreadPoolExecutor asyncContextExecutor) {
        Preconditions.checkState(request != null, "create async context err because of request is null");
        this.request = request;
        this.response = response;
        this.asyncContextExecutor = asyncContextExecutor;
    }

    public void onComplete(final T response) {
        Preconditions.checkState(Objects.nonNull(response), "response cant be null");
        this.response = response;
        this.complete.compareAndSet(Boolean.FALSE, Boolean.TRUE);
    }

    public void onComplete(final T response, CompleteHandler<T> handler) {
        Preconditions.checkState(Objects.nonNull(response), "response cant be null");
        Preconditions.checkState(Objects.nonNull(handler), "handler cant be null");
        this.response = response;
        CompletableFuture.runAsync(() -> {
            handler.onResponse(response);
        }, asyncContextExecutor);
        this.complete.compareAndSet(Boolean.FALSE, Boolean.TRUE);
    }

    public boolean isComplete() {
        return complete.get();
    }

    public T getRequest() {
        return request;
    }

    public void setRequest(T request) {
        this.request = request;
    }

    public T getResponse() {
        return response;
    }

    public void setResponse(T response) {
        this.response = response;
    }

    public ThreadPoolExecutor getAsyncContextExecutor() {
        return asyncContextExecutor;
    }

    public void setAsyncContextExecutor(ThreadPoolExecutor asyncContextExecutor) {
        this.asyncContextExecutor = asyncContextExecutor;
    }
}

package org.apache.rocketmq.proxy.core.protocol.http.processor;

import io.netty.channel.ChannelHandlerContext;
import org.apache.rocketmq.proxy.common.command.HttpCommand;
import org.apache.rocketmq.proxy.core.protocol.http.async.AsyncContext;

public interface HttpRequestProcessor {
    void processRequest(final ChannelHandlerContext ctx, final AsyncContext<HttpCommand> asyncContext)
        throws Exception;

    boolean rejectRequest();
}

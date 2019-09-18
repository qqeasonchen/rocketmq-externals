package org.apache.rocketmq.proxy.core.protocol.http.processor;

import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.proxy.boot.MQProxyHttpServer;
import org.apache.rocketmq.proxy.boot.MQProxyServer;
import org.apache.rocketmq.proxy.common.ProxyConstants;
import org.apache.rocketmq.proxy.common.ProxyResponseCode;
import org.apache.rocketmq.proxy.common.RequestCode;
import org.apache.rocketmq.proxy.common.command.HttpCommand;
import org.apache.rocketmq.proxy.common.protocol.http.body.SendMessageRequestBody;
import org.apache.rocketmq.proxy.common.protocol.http.body.SendMessageResponseBody;
import org.apache.rocketmq.proxy.common.MessageType;
import org.apache.rocketmq.proxy.common.protocol.http.header.SendMessageRequestHeader;
import org.apache.rocketmq.proxy.common.protocol.http.header.SendMessageResponseHeader;
import org.apache.rocketmq.proxy.core.protocol.http.async.AsyncContext;
import org.apache.rocketmq.proxy.core.protocol.http.async.CompleteHandler;
import org.apache.rocketmq.proxy.core.protocol.http.producer.ProxyProducer;
import org.apache.rocketmq.proxy.core.protocol.http.producer.SendMessageContext;
import org.apache.rocketmq.proxy.utils.ProxyUtil;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.common.RemotingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendMessageProcessor implements HttpRequestProcessor {
    public Logger messageLogger = LoggerFactory.getLogger("message");
    public Logger cmdLogger = LoggerFactory.getLogger("cmd");
    private MQProxyServer mQProxyServer;
    private MQProxyHttpServer mQProxyHttpServer;

    public SendMessageProcessor(MQProxyServer mQProxyServer) {
        this.mQProxyServer = mQProxyServer;
        this.mQProxyHttpServer = mQProxyServer.getHttpServer();
    }

    @Override
    public void processRequest(ChannelHandlerContext ctx, AsyncContext<HttpCommand> asyncContext) throws Exception {

        HttpCommand responseProxyCommand;

        cmdLogger.info("cmd={}|{}|client2proxy|from={}|to={}", RequestCode.get(Integer.valueOf(asyncContext.getRequest().getRequestCode())),
            ProxyConstants.PROTOCOL_HTTP,
            RemotingHelper.parseChannelRemoteAddr(ctx.channel()), RemotingUtil.getLocalAddress());

        SendMessageRequestHeader sendMessageRequestHeader = (SendMessageRequestHeader) asyncContext.getRequest().getHeader();
        SendMessageRequestBody sendMessageRequestBody = (SendMessageRequestBody) asyncContext.getRequest().getBody();

        SendMessageResponseHeader sendMessageResponseHeader =
            SendMessageResponseHeader.buildHeader(Integer.valueOf(asyncContext.getRequest().getRequestCode()), mQProxyServer.getProxyConfiguration().proxyCluster,
                RemotingUtil.getLocalAddress(), mQProxyServer.getProxyConfiguration().proxyEnv,
                mQProxyServer.getProxyConfiguration().proxyRegion,
                mQProxyServer.getProxyConfiguration().proxyDCN, mQProxyServer.getProxyConfiguration().proxyIDC);

        if (StringUtils.isBlank(sendMessageRequestHeader.getGroupName())
            || StringUtils.isBlank(sendMessageRequestHeader.getPid())
            || !StringUtils.isNumeric(sendMessageRequestHeader.getPid())) {
            responseProxyCommand = asyncContext.getRequest().createHttpCommandResponse(
                sendMessageResponseHeader,
                SendMessageResponseBody.buildBody(ProxyResponseCode.PROXY_PROTOCOL_HEADER_ERR.getRetCode(), ProxyResponseCode.PROXY_PROTOCOL_HEADER_ERR.getErrMsg()));
            asyncContext.onComplete(responseProxyCommand);
            return;
        }

        if (StringUtils.isBlank(sendMessageRequestBody.getBizSeqNo())
            || StringUtils.isBlank(sendMessageRequestBody.getUniqueId())
            || StringUtils.isBlank(sendMessageRequestBody.getTopic())
            || StringUtils.isBlank(sendMessageRequestBody.getContent())
            || (StringUtils.isBlank(sendMessageRequestBody.getTtl())
            && String.valueOf(MessageType.SYNC.getType()).equals(sendMessageRequestBody.getMessageType()))) {
            responseProxyCommand = asyncContext.getRequest().createHttpCommandResponse(
                sendMessageResponseHeader,
                SendMessageResponseBody.buildBody(ProxyResponseCode.PROXY_PROTOCOL_BODY_ERR.getRetCode(), ProxyResponseCode.PROXY_PROTOCOL_BODY_ERR.getErrMsg()));
            asyncContext.onComplete(responseProxyCommand);
            return;
        }

        messageLogger.info("message|client2Proxy|{}|clientIp={}|pid={}|topic={}|bizSeqNo={}|uniqueId={}",
            MessageType.get(Integer.valueOf(sendMessageRequestBody.getMessageType())),
            sendMessageRequestHeader.getIp(),
            sendMessageRequestHeader.getPid(),
            sendMessageRequestBody.getTopic(),
            sendMessageRequestBody.getBizSeqNo(),
            sendMessageRequestBody.getUniqueId());

        String producerGroup = sendMessageRequestHeader.getGroupName();
        ProxyProducer proxyProducer = mQProxyServer.getProducerManager().getProxyProducer(producerGroup);

        if (!proxyProducer.getStarted().get()) {
            responseProxyCommand = asyncContext.getRequest().createHttpCommandResponse(
                sendMessageResponseHeader,
                SendMessageResponseBody.buildBody(ProxyResponseCode.PROXY_GROUP_PRODUCER_STOPED_ERR.getRetCode(), ProxyResponseCode.PROXY_GROUP_PRODUCER_STOPED_ERR.getErrMsg()));
            asyncContext.onComplete(responseProxyCommand);
            return;
        }

        String ttl = String.valueOf(ProxyConstants.DEFAULT_MSG_TTL_MILLS);
        if (StringUtils.isNotBlank(sendMessageRequestBody.getTtl()) && StringUtils.isNumeric(sendMessageRequestBody.getTtl())) {
            ttl = sendMessageRequestBody.getTtl();
        }

        Message rocketMQMsg;
        try {
            rocketMQMsg = new Message(sendMessageRequestBody.getTopic(),
                sendMessageRequestBody.getContent().getBytes(ProxyConstants.DEFAULT_CHARSET));
            rocketMQMsg.getProperties().put(ProxyConstants.REQ_C2PROXY_TIMESTAMP, String.valueOf(System.currentTimeMillis()));
            if (messageLogger.isDebugEnabled()) {
                messageLogger.debug("build Message success, bizSeqNo={}, topic={}", sendMessageRequestBody.getBizSeqNo(),
                    sendMessageRequestBody.getTopic());
            }

        } catch (Exception e) {
            messageLogger.error("build Message error, bizSeqNo={}, topic={}", sendMessageRequestBody.getBizSeqNo(),
                sendMessageRequestBody.getTopic(), e);
            responseProxyCommand = asyncContext.getRequest().createHttpCommandResponse(
                sendMessageResponseHeader,
                SendMessageResponseBody.buildBody(ProxyResponseCode.PROXY_PACKAGE_MSG_ERR.getRetCode(), ProxyResponseCode.PROXY_PACKAGE_MSG_ERR.getErrMsg() + ProxyUtil.stackTrace(e, 2)));
            asyncContext.onComplete(responseProxyCommand);
            return;
        }

        final SendMessageContext sendMessageContext = new SendMessageContext(rocketMQMsg);
        final CompleteHandler<HttpCommand> handler = (new CompleteHandler<HttpCommand>() {
            @Override
            public void onResponse(HttpCommand httpCommand) {
                try {
                    mQProxyHttpServer.sendResponse(ctx, httpCommand.httpResponse());
                } catch (Exception ex) {
                    messageLogger.warn("send response fail.", ex);
                }
            }
        });

        if (String.valueOf(MessageType.ASYNC.getType()).equals(sendMessageRequestBody.getMessageType())) {
            asyncMessage(asyncContext, sendMessageContext, sendMessageRequestHeader, sendMessageRequestBody, proxyProducer, sendMessageResponseHeader, handler);
        }
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }

    private void asyncMessage(AsyncContext<HttpCommand> asyncContext,
        SendMessageContext sendMessageContext,
        SendMessageRequestHeader sendMessageRequestHeader,
        SendMessageRequestBody sendMessageRequestBody,
        ProxyProducer proxyProducer,
        SendMessageResponseHeader sendMessageResponseHeader,
        CompleteHandler<HttpCommand> handler) {

        long startTime = System.currentTimeMillis();

        try {
            sendMessageContext.getMsg().getProperties().put(ProxyConstants.REQ_PROXY2BROKER_TIMESTAMP, String.valueOf(System.currentTimeMillis()));
            proxyProducer.send(sendMessageContext, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    HttpCommand succ = asyncContext.getRequest().createHttpCommandResponse(
                        sendMessageResponseHeader,
                        SendMessageResponseBody.buildBody(ProxyResponseCode.SUCCESS.getRetCode(), sendResult.toString()/*ProxyResponseCode.SUCCESS.getErrMsg()*/));
                    asyncContext.onComplete(succ, handler);
                }

                @Override
                public void onException(Throwable e) {
                    HttpCommand err = asyncContext.getRequest().createHttpCommandResponse(
                        sendMessageResponseHeader,
                        SendMessageResponseBody.buildBody(ProxyResponseCode.PROXY_SEND_ASYNC_MSG_ERR.getRetCode(),
                            ProxyResponseCode.PROXY_SEND_ASYNC_MSG_ERR.getErrMsg() + ProxyUtil.stackTrace(e, 2)));
                    asyncContext.onComplete(err, handler);
                }
            });
        } catch (Exception ex) {
            messageLogger.error("sendMsg2broker err, bizSeqNo={}, topic={}", sendMessageRequestBody.getBizSeqNo(),
                sendMessageRequestBody.getTopic(), ex);
            HttpCommand err = asyncContext.getRequest().createHttpCommandResponse(
                sendMessageResponseHeader,
                SendMessageResponseBody.buildBody(ProxyResponseCode.PROXY_SEND_ASYNC_MSG_ERR.getRetCode(),
                    ProxyResponseCode.PROXY_SEND_ASYNC_MSG_ERR.getErrMsg() + ProxyUtil.stackTrace(ex, 2)));
            asyncContext.onComplete(err);
        }

        long endTime = System.currentTimeMillis();
        messageLogger.info("message|proxy2broker|{}|clientIp={}|pid={}|cost={}ms|topic={}|bizSeqNo={}|uniqueId={}",
            MessageType.get(Integer.valueOf(sendMessageRequestBody.getMessageType())),
            sendMessageRequestHeader.getIp(),
            sendMessageRequestHeader.getPid(),
            endTime - startTime,
            sendMessageRequestBody.getTopic(),
            sendMessageRequestBody.getBizSeqNo(),
            sendMessageRequestBody.getUniqueId());
    }
}

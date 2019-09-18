package org.apache.rocketmq.proxy.core.protocol.http.processor;

import io.netty.channel.ChannelHandlerContext;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.Validators;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageBatch;
import org.apache.rocketmq.common.message.MessageClientIDSetter;
import org.apache.rocketmq.proxy.boot.MQProxyServer;
import org.apache.rocketmq.proxy.common.ProxyConstants;
import org.apache.rocketmq.proxy.common.ProxyResponseCode;
import org.apache.rocketmq.proxy.common.RequestCode;
import org.apache.rocketmq.proxy.common.command.HttpCommand;
import org.apache.rocketmq.proxy.common.protocol.http.body.SendMessageBatchRequestBody;
import org.apache.rocketmq.proxy.common.protocol.http.body.SendMessageBatchResponseBody;
import org.apache.rocketmq.proxy.common.MessageType;
import org.apache.rocketmq.proxy.common.protocol.http.header.SendMessageBatchRequestHeader;
import org.apache.rocketmq.proxy.common.protocol.http.header.SendMessageBatchResponseHeader;
import org.apache.rocketmq.proxy.core.protocol.http.async.AsyncContext;
import org.apache.rocketmq.proxy.core.protocol.http.producer.ProxyProducer;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.common.RemotingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendBatchMessageProcessor implements HttpRequestProcessor {
    public Logger cmdLogger = LoggerFactory.getLogger("cmd");
    private MQProxyServer mQProxyServer;
    public SendBatchMessageProcessor(MQProxyServer mQProxyServer) {
        this.mQProxyServer = mQProxyServer;
    }
    public Logger batchMessageLogger = LoggerFactory.getLogger("batchMessage");

    public void processRequest(ChannelHandlerContext ctx, AsyncContext<HttpCommand> asyncContext) throws Exception {
        HttpCommand responseProxyCommand;
        cmdLogger.info("cmd={}|{}|client2proxy|from={}|to={}", RequestCode.get(Integer.valueOf(asyncContext.getRequest().getRequestCode())),
            ProxyConstants.PROTOCOL_HTTP,
            RemotingHelper.parseChannelRemoteAddr(ctx.channel()), RemotingUtil.getLocalAddress());

        SendMessageBatchRequestHeader sendMessageBatchRequestHeader = (SendMessageBatchRequestHeader) asyncContext.getRequest().getHeader();
        SendMessageBatchRequestBody sendMessageBatchRequestBody = (SendMessageBatchRequestBody) asyncContext.getRequest().getBody();

        SendMessageBatchResponseHeader sendMessageBatchResponseHeader =
            SendMessageBatchResponseHeader.buildHeader(Integer.valueOf(asyncContext.getRequest().getRequestCode()), mQProxyServer.getProxyConfiguration().proxyCluster,
                RemotingUtil.getLocalAddress(), mQProxyServer.getProxyConfiguration().proxyEnv,
                mQProxyServer.getProxyConfiguration().proxyRegion,
                mQProxyServer.getProxyConfiguration().proxyDCN, mQProxyServer.getProxyConfiguration().proxyIDC);

        if (StringUtils.isBlank(sendMessageBatchRequestHeader.getPid())
            || !StringUtils.isNumeric(sendMessageBatchRequestHeader.getPid())
            || StringUtils.isBlank(sendMessageBatchRequestHeader.getSys())
            || StringUtils.isBlank(sendMessageBatchRequestHeader.getGroupName())) {
            responseProxyCommand = asyncContext.getRequest().createHttpCommandResponse(
                sendMessageBatchResponseHeader,
                SendMessageBatchResponseBody.buildBody(ProxyResponseCode.PROXY_PROTOCOL_HEADER_ERR.getRetCode(), ProxyResponseCode.PROXY_PROTOCOL_HEADER_ERR.getErrMsg()));
            asyncContext.onComplete(responseProxyCommand);
            return;
        }

        if (CollectionUtils.isEmpty(sendMessageBatchRequestBody.getContents())
            || StringUtils.isBlank(sendMessageBatchRequestBody.getBatchId())
            || (Integer.valueOf(sendMessageBatchRequestBody.getSize()) != CollectionUtils.size(sendMessageBatchRequestBody.getContents()))) {
            responseProxyCommand = asyncContext.getRequest().createHttpCommandResponse(
                sendMessageBatchResponseHeader,
                SendMessageBatchResponseBody.buildBody(ProxyResponseCode.PROXY_PROTOCOL_BODY_ERR.getRetCode(), ProxyResponseCode.PROXY_PROTOCOL_BODY_ERR.getErrMsg()));
            asyncContext.onComplete(responseProxyCommand);
            return;
        }

        batchMessageLogger.info("batchMessage|client2Proxy|REQ|{}|batchId={}|msgNum={}",
            MessageType.ASYNC,
            sendMessageBatchRequestBody.getBatchId(),
            sendMessageBatchRequestBody.getSize());

        if (!mQProxyServer.getProxyConfiguration().proxyServerBatchMsgNumLimiter
            .tryAcquire(Integer.valueOf(sendMessageBatchRequestBody.getSize()), ProxyConstants.DEFAULT_FASTFAIL_TIMEOUT_IN_MILLISECONDS, TimeUnit.MILLISECONDS)) {
            responseProxyCommand = asyncContext.getRequest().createHttpCommandResponse(
                sendMessageBatchResponseHeader,
                SendMessageBatchResponseBody.buildBody(ProxyResponseCode.PROXY_BATCH_SPEED_OVER_LIMIT_ERR.getRetCode(), ProxyResponseCode.PROXY_BATCH_SPEED_OVER_LIMIT_ERR.getErrMsg()));
            asyncContext.onComplete(responseProxyCommand);
            return;
        }

        String producerGroup = sendMessageBatchRequestHeader.getGroupName();
        ProxyProducer batchProxyProducer = mQProxyServer.getProducerManager().getProxyProducer(producerGroup);

        if (!batchProxyProducer.getStarted().get()) {
            responseProxyCommand = asyncContext.getRequest().createHttpCommandResponse(
                sendMessageBatchResponseHeader,
                SendMessageBatchResponseBody.buildBody(ProxyResponseCode.PROXY_BATCH_PRODUCER_STOPED_ERR.getRetCode(), ProxyResponseCode.PROXY_BATCH_PRODUCER_STOPED_ERR.getErrMsg()));
            asyncContext.onComplete(responseProxyCommand);
            return;
        }

        long batchStartTime = System.currentTimeMillis();
        List<Message> msgList = new ArrayList<>();
        boolean buildMessageOk = true;
        for (SendMessageBatchRequestBody.BatchMessageEntity msg : sendMessageBatchRequestBody.getContents()) {
            if (StringUtils.isBlank(msg.topic)
                || StringUtils.isBlank(msg.msg)) {
                buildMessageOk = false;
                break;
            }

            if (StringUtils.isBlank(msg.ttl) || !StringUtils.isNumeric(msg.ttl)) {
                msg.ttl = String.valueOf(ProxyConstants.DEFAULT_MSG_TTL_MILLS);
            }

            try {
                Message rocketMQMsg;
                if (StringUtils.isBlank(msg.tag)) {
                    rocketMQMsg = new Message(msg.topic, msg.msg.getBytes(ProxyConstants.DEFAULT_CHARSET));
                } else {
                    rocketMQMsg = new Message(msg.topic, msg.tag, msg.msg.getBytes(ProxyConstants.DEFAULT_CHARSET));
                }
                msgList.add(rocketMQMsg);
                if (batchMessageLogger.isDebugEnabled()) {
                    batchMessageLogger.debug("build Message success, msg:{}", msg.msg);
                }

            } catch (Exception e) {
                buildMessageOk = false;
                batchMessageLogger.error("build Message error, msg:{}", msg, e);
            }
        }

        if (!buildMessageOk || CollectionUtils.isEmpty(msgList)) {
            responseProxyCommand = asyncContext.getRequest().createHttpCommandResponse(
                sendMessageBatchResponseHeader,
                SendMessageBatchResponseBody.buildBody(ProxyResponseCode.PROXY_PACKAGE_MSG_ERR.getRetCode(), ProxyResponseCode.PROXY_PACKAGE_MSG_ERR.getErrMsg()));
            asyncContext.onComplete(responseProxyCommand);
            return;
        }

        SendMessageBatchResponseBody responseBody = SendMessageBatchResponseBody.buildBody(ProxyResponseCode.SUCCESS.getRetCode(), ProxyResponseCode.SUCCESS.getErrMsg());
        try {
            MessageBatch msgBatch = MessageBatch.generateFromList(msgList);
            for (Message message : msgBatch) {
                Validators.checkMessage(message, batchProxyProducer.getMQProducer());
                MessageClientIDSetter.setUniqID(message);
            }
            msgBatch.setBody(msgBatch.encode());

            SendResult sendResult = batchProxyProducer.getMQProducer().send(msgBatch, 3000);

            long batchEndTime = System.currentTimeMillis();
            if (batchMessageLogger.isDebugEnabled()) {
                batchMessageLogger.debug("batchMessage|proxy2broker|REQ|{}|batchId={}|cost={}ms|msgNum={}|topic={}",
                    MessageType.ASYNC,
                    sendMessageBatchRequestBody.getBatchId(),
                    batchEndTime - batchStartTime,
                    sendMessageBatchRequestBody.getSize(),
                    msgList.get(0).getTopic());
            }

            if (sendResult != null) {
                responseBody.setRetMsg(sendResult.toString());
            }
            responseBody.setResTime(System.currentTimeMillis());
            responseProxyCommand = asyncContext.getRequest().createHttpCommandResponse(
                sendMessageBatchResponseHeader, responseBody);
            asyncContext.onComplete(responseProxyCommand);

        } catch (Exception e) {
            batchMessageLogger.warn("", e);
        }
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}

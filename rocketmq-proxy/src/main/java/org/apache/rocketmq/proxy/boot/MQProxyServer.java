package org.apache.rocketmq.proxy.boot;

import org.apache.rocketmq.proxy.common.RequestCode;
import org.apache.rocketmq.proxy.configuration.ProxyConfiguration;
import org.apache.rocketmq.proxy.core.protocol.http.processor.SendBatchMessageProcessor;
import org.apache.rocketmq.proxy.core.protocol.http.processor.SendMessageProcessor;
import org.apache.rocketmq.proxy.core.protocol.http.producer.ProducerManager;
import org.apache.rocketmq.proxy.utils.ProxyUtil;
import org.apache.rocketmq.proxy.utils.ThreadUtil;

public class MQProxyServer extends MQProxyServerAssist {

    public MQProxyHttpServer httpServer;
    private ProducerManager producerManager;

    public MQProxyServer(ProxyConfiguration proxyConfiguration) {
        super(proxyConfiguration);
    }

    public void init() throws Exception {
        initThreadPool();
        producerManager = new ProducerManager(this);
        producerManager.init();
        httpServer = new MQProxyHttpServer(this);
        httpServer.init("proxy-http");
        this.registerHttpRequestProcessor();

        jsonMapper = ProxyUtil.createJsoner();
    }

    public void start() throws Exception {
        producerManager.start();
        httpServer.start();
    }

    public void shutdown() throws Exception {
        httpServer.shutdown();
        super.shutdownThreadPool();
        producerManager.shutdown();
        ThreadUtil.randomSleep(10, 30);
        long startTime = System.currentTimeMillis();
        scheduler.shutdown();
        logger.info("MQProxyServer shutdown, cost=[{}]ms", String.valueOf(System.currentTimeMillis() - startTime));

    }

    public void registerHttpRequestProcessor() {
        SendBatchMessageProcessor sendBatchMessageProcessor = new SendBatchMessageProcessor(this);
        httpServer.registerProcessor(RequestCode.MSG_BATCH_SEND.getRequestCode(),
            sendBatchMessageProcessor, batchMsgExecutor);

        SendMessageProcessor sendMessageProcessor = new SendMessageProcessor(this);
        httpServer.registerProcessor(RequestCode.MSG_SEND.getRequestCode(),
            sendMessageProcessor, sendMsgExecutor);
    }

    public MQProxyHttpServer getHttpServer() {
        return httpServer;
    }

    public ProducerManager getProducerManager() {
        return producerManager;
    }
}

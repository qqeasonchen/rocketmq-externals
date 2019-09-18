package org.apache.rocketmq.proxy.core.protocol.http.producer;

import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.proxy.configuration.ProxyConfiguration;
import org.apache.rocketmq.proxy.utils.ProxyUtil;
import org.apache.rocketmq.proxy.utils.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyProducer {
    protected AtomicBoolean started = new AtomicBoolean(Boolean.FALSE);

    protected AtomicBoolean inited = new AtomicBoolean(Boolean.FALSE);

    public Logger logger = LoggerFactory.getLogger(this.getClass());

    public AtomicBoolean getInited() {
        return inited;
    }

    public AtomicBoolean getStarted() {
        return started;
    }

    protected ProducerGroupConfig producerGroupConfig;

    protected ProxyConfiguration proxyConfiguration;

    protected DefaultMQProducer mQProducer;

    public void send(SendMessageContext sendMsgContext, SendCallback sendCallback) throws Exception {
        mQProducer.send(sendMsgContext.getMsg(), sendCallback);
    }

    public DefaultMQProducer getMQProducer() {
        return mQProducer;
    }

    public synchronized void init(ProxyConfiguration proxyConfiguration, ProducerGroupConfig producerGroupConfig) {
        this.producerGroupConfig = producerGroupConfig;
        this.proxyConfiguration = proxyConfiguration;

        mQProducer = new DefaultMQProducer(producerGroupConfig.getGroupName(), null);
        mQProducer.setNamesrvAddr(proxyConfiguration.namesrvAddrs);
        mQProducer.setVipChannelEnabled(false);
        String proxyId = ProxyUtil.buildProxyClientID(proxyConfiguration.sysID,
            proxyConfiguration.proxyRegion,
            proxyConfiguration.proxyDCN,
            proxyConfiguration.proxyCluster);
        mQProducer.setInstanceName(buildProducerInstanceId(proxyId, producerGroupConfig));
        mQProducer.setCompressMsgBodyOverHowmuch(2 * 1024);
        inited.compareAndSet(false, true);
        logger.info("ProxyProducer [{}] inited.............", producerGroupConfig.getGroupName());
    }

    public synchronized void start() throws Exception {
        if (started.get()) {
            return;
        }
        mQProducer.start();
        started.compareAndSet(false, true);
        ThreadUtil.randomSleep(500);
        mQProducer.getDefaultMQProducerImpl().getmQClientFactory().updateTopicRouteInfoFromNameServer();
        logger.info("ProxyProducer [{}] started.............", producerGroupConfig.getGroupName());
    }

    public synchronized void shutdown() throws Exception {
        if (!inited.get()) {
            return;
        }

        if (!started.get()) {
            return;
        }
        mQProducer.shutdown();
        inited.compareAndSet(true, false);
        started.compareAndSet(true, false);
        logger.info("ProxyProducer [{}] shutdown.............", producerGroupConfig.getGroupName());
    }

    private String buildProducerInstanceId(String proxyId, ProducerGroupConfig producerGroupConfig) {
        return proxyId + "#" + producerGroupConfig.getGroupName();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("proxyProducer={")
            .append("inited=").append(inited.get()).append(",")
            .append("started=").append(started.get()).append(",")
            .append("producerGroupConfig=").append(producerGroupConfig).append("}");
        return sb.toString();
    }
}

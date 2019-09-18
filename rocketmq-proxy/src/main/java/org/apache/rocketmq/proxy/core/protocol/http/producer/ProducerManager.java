package org.apache.rocketmq.proxy.core.protocol.http.producer;

import java.util.concurrent.ConcurrentHashMap;
import org.apache.rocketmq.proxy.boot.MQProxyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerManager {
    public Logger logger = LoggerFactory.getLogger(this.getClass());

    private MQProxyServer mQProxyServer;

    private ConcurrentHashMap<String /** groupName*/, ProxyProducer> producerTable = new ConcurrentHashMap<String, ProxyProducer>();

    public ProducerManager(MQProxyServer mQProxyServer) {
        this.mQProxyServer = mQProxyServer;
    }

    public void init() throws Exception {
        logger.info("producerManager inited");
    }

    public void start() throws Exception {
        logger.info("producerManager started");
    }

    public ProxyProducer getProxyProducer(String producerGroup) throws Exception {
        ProxyProducer proxyProducer = null;
        if (!producerTable.containsKey(producerGroup)) {
            synchronized (producerTable) {
                if (!producerTable.containsKey(producerGroup)) {
                    ProducerGroupConfig producerGroupConfig = new ProducerGroupConfig(producerGroup);
                    proxyProducer = createProxyProducer(producerGroupConfig);
                    proxyProducer.start();
                }
            }
        }

        proxyProducer = producerTable.get(producerGroup);

        if (!proxyProducer.getStarted().get()) {
            proxyProducer.start();
        }

        return proxyProducer;
    }

    public synchronized ProxyProducer createProxyProducer(ProducerGroupConfig producerGroupConfig) throws Exception {
        if (producerTable.containsKey(producerGroupConfig.getGroupName())) {
            return producerTable.get(producerGroupConfig.getGroupName());
        }
        ProxyProducer proxyProducer = new ProxyProducer();
        proxyProducer.init(mQProxyServer.getProxyConfiguration(), producerGroupConfig);
        producerTable.put(producerGroupConfig.getGroupName(), proxyProducer);
        return proxyProducer;
    }

    public void shutdown() {
        for (ProxyProducer proxyProducer : producerTable.values()) {
            try {
                proxyProducer.shutdown();
            } catch (Exception ex) {
                logger.error("shutdown proxyProducer[{}] err", proxyProducer, ex);
            }
        }
    }

    public MQProxyServer getMQProxyServer() {
        return mQProxyServer;
    }

    public ConcurrentHashMap<String, ProxyProducer> getProducerTable() {
        return producerTable;
    }
}

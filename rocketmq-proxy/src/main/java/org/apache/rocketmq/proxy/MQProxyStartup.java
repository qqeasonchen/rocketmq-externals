package org.apache.rocketmq.proxy;

import org.apache.rocketmq.proxy.boot.MQProxyServer;
import org.apache.rocketmq.proxy.configuration.ProxyConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MQProxyStartup {
    public static Logger logger = LoggerFactory.getLogger(MQProxyStartup.class);

    public static void main(String[] args) throws Exception {
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
        proxyConfiguration.init();
        MQProxyServer server = new MQProxyServer(proxyConfiguration);
        server.init();
        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                logger.info("MQProxy shutting down hook begin...");
                long start = System.currentTimeMillis();
                server.shutdown();
                long end = System.currentTimeMillis();
                logger.info("MQProxy shutdown cost {}ms", end - start);
            } catch (Exception e) {
                logger.error("exception when MQProxy shutdown...", e);
            }
        }));
    }
}

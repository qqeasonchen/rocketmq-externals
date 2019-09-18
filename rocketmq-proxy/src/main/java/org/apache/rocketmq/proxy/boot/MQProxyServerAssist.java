package org.apache.rocketmq.proxy.boot;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.rocketmq.proxy.configuration.ProxyConfiguration;
import org.apache.rocketmq.proxy.utils.ThreadPoolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MQProxyServerAssist {
    public Logger logger = LoggerFactory.getLogger(this.getClass());

    public ProxyConfiguration proxyConfiguration;
    public static ObjectMapper jsonMapper;
    public ThreadPoolExecutor batchMsgExecutor;
    public ThreadPoolExecutor sendMsgExecutor;
    public ThreadPoolExecutor clientManageExecutor;
    public ThreadPoolExecutor adminExecutor;
    public static ScheduledExecutorService scheduler;

    public MQProxyServerAssist(ProxyConfiguration proxyConfiguration) {
        this.proxyConfiguration = proxyConfiguration;
    }

    public void shutdownThreadPool() {
        batchMsgExecutor.shutdown();
        adminExecutor.shutdown();
        clientManageExecutor.shutdown();
        sendMsgExecutor.shutdown();
    }

    public void initThreadPool() {
        BlockingQueue<Runnable> batchMsgThreadPoolQueue = new LinkedBlockingQueue<Runnable>(proxyConfiguration.proxyServerBatchBlockQSize);
        batchMsgExecutor = ThreadPoolFactory.createThreadPoolExecutor(proxyConfiguration.proxyServerBatchMsgThreadNum,
            proxyConfiguration.proxyServerBatchMsgThreadNum, batchMsgThreadPoolQueue, "proxy-batchmsg-", true);

        BlockingQueue<Runnable> sendMsgThreadPoolQueue = new LinkedBlockingQueue<Runnable>(proxyConfiguration.proxyServerSendMsgBlockQSize);
        sendMsgExecutor = ThreadPoolFactory.createThreadPoolExecutor(proxyConfiguration.proxyServerSendMsgThreadNum,
            proxyConfiguration.proxyServerSendMsgThreadNum, sendMsgThreadPoolQueue, "proxy-sendmsg-", true);

        BlockingQueue<Runnable> clientManageThreadPoolQueue = new LinkedBlockingQueue<Runnable>(proxyConfiguration.proxyServerClientManageBlockQSize);
        clientManageExecutor = ThreadPoolFactory.createThreadPoolExecutor(proxyConfiguration.proxyServerClientManageThreadNum,
            proxyConfiguration.proxyServerClientManageThreadNum, clientManageThreadPoolQueue, "proxy-clientmanage-", true);

        BlockingQueue<Runnable> adminThreadPoolQueue = new LinkedBlockingQueue<Runnable>(50);
        adminExecutor = ThreadPoolFactory.createThreadPoolExecutor(proxyConfiguration.proxyServerAdminThreadNum,
            proxyConfiguration.proxyServerAdminThreadNum, adminThreadPoolQueue, "proxy-admin-", true);

        scheduler = ThreadPoolFactory.createScheduledExecutor(proxyConfiguration.proxyTcpGlobalScheduler, "proxy-tcp-scheduler-");
    }

    public ProxyConfiguration getProxyConfiguration() {
        return proxyConfiguration;
    }
}

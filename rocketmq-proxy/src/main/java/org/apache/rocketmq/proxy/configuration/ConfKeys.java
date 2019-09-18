package org.apache.rocketmq.proxy.configuration;

public class ConfKeys {
    public static String KEYS_PROXY_ENV = "proxy.server.env";
    public static String KEYS_PROXY_REGION = "proxy.server.region";
    public static String KEYS_PROXY_IDC = "proxy.server.idc";
    public static String KEYS_PROXY_DCN = "proxy.server.dcn";
    public static String KEYS_PROXY_SYSID = "proxy.systemId";
    public static String KEYS_PROXY_SERVER_CLUSTER = "proxy.server.cluster";
    public static String KEYS_PROXY_SERVER_HTTP_PORT = "proxy.server.http.port";
    public static String KEYS_PROXY_BATCHMSG_THREAD_NUM = "proxy.server.batchmsg.threads.num";
    public static String KEYS_PROXY_BATCHMSG_RATELIMITER = "proxy.server.batchmsg.speed.ratelimiter";
    public static String KEYS_PROXY_BATCHMSG_BATCH_ENABLED = "proxy.server.batchmsg.batch.enabled";
    public static String KEYS_PROXY_ASYNC_ACCUMULATION_THRESHOLD = "proxy.server.async.accumulation.threshold";
    public static String KEY_PROXY_BUSY_CHECK_INTERVEL = "proxy.server.busy.check.intervel";
    public static String KEYS_PROXY_SENDMSG_THREAD_NUM = "proxy.server.sendmsg.threads.num";
    public static String KEYS_PROXY_REPLYMSG_THREAD_NUM = "proxy.server.replymsg.threads.num";
    public static String KEYS_PROXY_PUSHMSG_THREAD_NUM = "proxy.server.pushmsg.threads.num";
    public static String KEYS_PROXY_REGISTRY_THREAD_NUM = "proxy.server.registry.threads.num";
    public static String KEYS_PROXY_CLIENTMANAGE_THREAD_NUM = "proxy.server.clientmanage.threads.num";
    public static String KEYS_PROXY_ADMIN_THREAD_NUM = "proxy.server.admin.threads.num";
    public static String KEY_PROXY_RETRY_THREAD_NUM = "proxy.server.retry.threads.num";
    public static String KEYS_PROXY_SERVICECENTER = "proxy.server.serviceCenter";
    public static String KEYS_PROXY_CONFIGCENTER = "proxy.server.configCenter";
    public static String KEYS_PROXY_REGISTRY_NAMESRV = "proxy.server.registry.namesrv";
    public static String KEYS_PROXY_PULL_REGISTRY_INTERVEL = "proxy.server.pull.registry.intervel";
    public static String KEY_PROXY_RETRY_BLOCKQ_SIZE = "proxy.server.retry.blockQ.size";
    public static String KEY_PROXY_BATCHMSG_BLOCKQ_SIZE = "proxy.server.batchmsg.blockQ.size";
    public static String KEY_PROXY_SENDMSG_BLOCKQ_SIZE = "proxy.server.sendmsg.blockQ.size";
    public static String KEY_PROXY_PUSHMSG_BLOCKQ_SIZE = "proxy.server.pushmsg.blockQ.size";
    public static String KEY_PROXY_CLIENTM_BLOCKQ_SIZE = "proxy.server.clientM.blockQ.size";
    public static String KEY_PROXY_CONSUMER_ENABLED = "proxy.server.consumer.enabled";
    public static String KEYS_PROXY_USERNAME = "proxy.server.username";
    public static String KEYS_PROXY_PASSWORD = "proxy.server.password";
    public static String KEYS_PROXY_CONSUME_THREADPOOL_MIN = "proxy.server.client.consumeThreadMin";
    public static String KEYS_PROXY_CONSUME_THREADPOOL_MAX = "proxy.server.client.consumeThreadMax";
    public static String KEYS_PROXY_CONSUME_THREADPOOL_QUEUESIZE = "proxy.server.client.consumeThreadPoolQueueSize";
    public static String KEYS_PROXY_CLIENT_ACK_WINDOW = "proxy.server.client.ackwindow";
    public static String KEYS_PROXY_CLIENT_PUB_WINDOW = "proxy.server.client.pubwindow";
    public static String KEYS_PROXY_CLIENT_PULL_BATCHSIZE = "proxy.server.client.pullBatchSize";
    public static String KEYS_PROXY_CLIENT_POLL_NAMESRV_INTERVEL = "proxy.server.client.pollNameServerInteval";
    public static String KEYS_PROXY_CLIENT_HEARTBEAT_BROKER_INTERVEL = "proxy.server.client.heartbeatBrokerInterval";
    public static String KEYS_PROXY_CLIENT_REBALANCE_INTERVEL = "proxy.server.client.rebalanceInterval";

    public static String KEYS_PROXY_SERVER_TCP_PORT = "proxy.server.tcp.port";
    public static String KEYS_PROXY_SERVER_READER_IDLE_SECONDS = "proxy.server.tcp.readerIdleSeconds";
    public static String KEYS_PROXY_SERVER_WRITER_IDLE_SECONDS = "proxy.server.tcp.writerIdleSeconds";
    public static String KEYS_PROXY_SERVER_ALL_IDLE_SECONDS = "proxy.server.tcp.allIdleSeconds";
    public static String KEYS_PROXY_SERVER_CLIENT_MAX_NUM = "proxy.server.tcp.clientMaxNum";
    public static String KEYS_PROXY_SERVER_TCP_REBALANCE_INTERVAL = "proxy.server.tcp.RebalanceIntervalInMills";
    public static String KEYS_PROXY_SERVER_GLOBAL_SCHEDULER = "proxy.server.global.scheduler";
    public static String KEYS_PROXY_SERVER_GLOBAL_ASYNC = "proxy.server.global.async";
    public static String KEYS_PROXY_SERVER_SESSION_EXPIRED_TIME = "proxy.server.session.expiredInMills";
    public static String KEYS_PROXY_SERVER_SESSION_UPSTREAM_BUFFER_SIZE = "proxy.server.session.upstreamBufferSize";
    public static String KEYS_PROXY_SERVER_SESSION_PUSH_RETRY_TIMES = "proxy.server.session.pushRetryTimes";
    public static String KEYS_PROXY_SERVER_SESSION_PUSH_RETRY_DELAY = "proxy.server.session.pushRetryDelayInMills";
    public static String KEYS_PROXY_SERVER_MONITOR_IMS_INTERFACE = "proxy.server.monitor.imsInterfaceName";
    public static String KEYS_PROXY_SERVER_MONITOR_IMS_ENABLED = "proxy.server.monitor.imsEnabled";
    public static String KEYS_PROXY_SERVER_DOUBLE_SEND_ENABLED = "proxy.server.doubleSend.enabled";
    public static String KEYS_PROXY_SERVER_DOUBLE_SEND_QUERY_CONFIG_INTERVAL = "proxy.server.doubleSend.queryConfigIntervalInSec";
    public static String KEYS_PROXY_SERVER_ADMIN_HTTP_PORT = "proxy.server.admin.http.port";
    public static String KEYS_PROXY_NAMESRV_ADDRS = "proxy.namesrv.addrs";

}

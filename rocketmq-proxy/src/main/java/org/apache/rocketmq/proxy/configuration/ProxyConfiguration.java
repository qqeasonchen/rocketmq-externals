package org.apache.rocketmq.proxy.configuration;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.RateLimiter;
import java.io.File;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.proxy.common.ProxyConstants;

public class ProxyConfiguration {
    public String proxyEnv;
    public String proxyRegion;
    public String proxyIDC;
    public String proxyDCN;
    public String proxyCluster;
    public String sysID;
    public int httpServerPort = 10105;

    public RateLimiter proxyServerBatchMsgNumLimiter = RateLimiter.create(50000);
    public boolean proxyServerBatchMsgBatchEnabled = Boolean.TRUE;
    public int proxyServerBatchMsgThreadNum = 8;
    public int proxyServerSendMsgThreadNum = 8;
    public int proxyServerClientManageThreadNum = 4;
    public int proxyServerAdminThreadNum = 2;
    public int proxyServerAsyncAccumulationThreshold = 100000;
    public int proxyServerBatchBlockQSize = 1000;
    public int proxyServerSendMsgBlockQSize = 1000;
    public int proxyServerClientManageBlockQSize = 1000;
    public String proxyUserName;
    public String proxyPasswd;
    public Integer pubWindow = 100;
    public Integer pollNameServerInteval = 10 * 1000;
    public Integer heartbeatBrokerInterval = 30 * 1000;
    public int proxyTcpGlobalScheduler = 5;
    public int proxyServerAdminPort = 10106;
    public String namesrvAddrs = "";

    public void init() {
        ConfigurationWraper configurationWraper = new ConfigurationWraper(ProxyConstants.PROXY_CONF_HOME + File.separator + "proxy.properties", false);

        String proxyEnvStr = configurationWraper.getProp(ConfKeys.KEYS_PROXY_ENV);
        Preconditions.checkState(StringUtils.isNotEmpty(proxyEnvStr), String.format("%s error", ConfKeys.KEYS_PROXY_ENV));
        proxyEnv = StringUtils.deleteWhitespace(proxyEnvStr);

        String proxyRegionStr = configurationWraper.getProp(ConfKeys.KEYS_PROXY_REGION);
        Preconditions.checkState(StringUtils.isNotEmpty(proxyRegionStr), String.format("%s error", ConfKeys.KEYS_PROXY_REGION));
        proxyRegion = StringUtils.deleteWhitespace(proxyRegionStr);

        String sysIdStr = configurationWraper.getProp(ConfKeys.KEYS_PROXY_SYSID);
        Preconditions.checkState(StringUtils.isNotEmpty(sysIdStr) && StringUtils.isNumeric(sysIdStr), String.format("%s error", ConfKeys.KEYS_PROXY_SYSID));
        sysID = StringUtils.deleteWhitespace(sysIdStr);

        String httpServerPortStr = configurationWraper.getProp(ConfKeys.KEYS_PROXY_SERVER_HTTP_PORT);
        Preconditions.checkState(StringUtils.isNotEmpty(httpServerPortStr) && StringUtils.isNumeric(httpServerPortStr), String.format("%s error", ConfKeys.KEYS_PROXY_SERVER_HTTP_PORT));
        httpServerPort = Integer.valueOf(StringUtils.deleteWhitespace(httpServerPortStr));

        String proxyClusterStr = configurationWraper.getProp(ConfKeys.KEYS_PROXY_SERVER_CLUSTER);
        Preconditions.checkState(StringUtils.isNotEmpty(proxyClusterStr), String.format("%s error", ConfKeys.KEYS_PROXY_SERVER_CLUSTER));
        proxyCluster = StringUtils.deleteWhitespace(proxyClusterStr);

        String proxyIDCStr = configurationWraper.getProp(ConfKeys.KEYS_PROXY_IDC);
        Preconditions.checkState(StringUtils.isNotEmpty(proxyIDCStr), String.format("%s error", ConfKeys.KEYS_PROXY_IDC));
        proxyIDC = StringUtils.deleteWhitespace(proxyIDCStr);

        String proxyDCNStr = configurationWraper.getProp(ConfKeys.KEYS_PROXY_DCN);
        Preconditions.checkState(StringUtils.isNotEmpty(proxyDCNStr), String.format("%s error", ConfKeys.KEYS_PROXY_DCN));
        proxyDCN = StringUtils.deleteWhitespace(proxyDCNStr);

        String proxyServerBatchMsgThreadNumStr = configurationWraper.getProp(ConfKeys.KEYS_PROXY_BATCHMSG_THREAD_NUM);
        if (StringUtils.isNotEmpty(proxyServerBatchMsgThreadNumStr) && StringUtils.isNumeric(proxyServerBatchMsgThreadNumStr)) {
            proxyServerBatchMsgThreadNum = Integer.valueOf(StringUtils.deleteWhitespace(proxyServerBatchMsgThreadNumStr));
        }

        String proxyServerBatchMsgNumLimiterStr = configurationWraper.getProp(ConfKeys.KEYS_PROXY_BATCHMSG_RATELIMITER);
        if (StringUtils.isNotEmpty(proxyServerBatchMsgNumLimiterStr) && StringUtils.isNumeric(proxyServerBatchMsgNumLimiterStr)) {
            proxyServerBatchMsgNumLimiter = RateLimiter.create(Double.valueOf(StringUtils.deleteWhitespace(proxyServerBatchMsgNumLimiterStr)));
        }

        String proxyServerBatchMsgBatchEnableStr = configurationWraper.getProp(ConfKeys.KEYS_PROXY_BATCHMSG_BATCH_ENABLED);
        if (StringUtils.isNotBlank(proxyServerBatchMsgBatchEnableStr)) {
            proxyServerBatchMsgBatchEnabled = Boolean.valueOf(proxyServerBatchMsgBatchEnableStr);
        }

        String proxyServerAsyncAccumulationThresholdStr = configurationWraper.getProp(ConfKeys.KEYS_PROXY_ASYNC_ACCUMULATION_THRESHOLD);
        if (StringUtils.isNotEmpty(proxyServerAsyncAccumulationThresholdStr) && StringUtils.isNumeric(proxyServerAsyncAccumulationThresholdStr)) {
            proxyServerAsyncAccumulationThreshold = Integer.valueOf(StringUtils.deleteWhitespace(proxyServerAsyncAccumulationThresholdStr));
        }

        String proxyServerSendMsgThreadNumStr = configurationWraper.getProp(ConfKeys.KEYS_PROXY_SENDMSG_THREAD_NUM);
        if (StringUtils.isNotEmpty(proxyServerSendMsgThreadNumStr) && StringUtils.isNumeric(proxyServerSendMsgThreadNumStr)) {
            proxyServerSendMsgThreadNum = Integer.valueOf(StringUtils.deleteWhitespace(proxyServerSendMsgThreadNumStr));
        }

        String proxyServerClientManageThreadNumStr = configurationWraper.getProp(ConfKeys.KEYS_PROXY_CLIENTMANAGE_THREAD_NUM);
        if (StringUtils.isNotEmpty(proxyServerClientManageThreadNumStr) && StringUtils.isNumeric(proxyServerClientManageThreadNumStr)) {
            proxyServerClientManageThreadNum = Integer.valueOf(StringUtils.deleteWhitespace(proxyServerClientManageThreadNumStr));
        }

        String proxyServerPullRegistryIntervelStr = configurationWraper.getProp(ConfKeys.KEYS_PROXY_PULL_REGISTRY_INTERVEL);
        if (StringUtils.isNotEmpty(proxyServerPullRegistryIntervelStr) && StringUtils.isNumeric(proxyServerPullRegistryIntervelStr)) {
            proxyServerClientManageThreadNum = Integer.valueOf(StringUtils.deleteWhitespace(proxyServerPullRegistryIntervelStr));
        }

        String proxyServerAdminThreadNumStr = configurationWraper.getProp(ConfKeys.KEYS_PROXY_ADMIN_THREAD_NUM);
        if (StringUtils.isNotEmpty(proxyServerAdminThreadNumStr) && StringUtils.isNumeric(proxyServerAdminThreadNumStr)) {
            proxyServerAdminThreadNum = Integer.valueOf(StringUtils.deleteWhitespace(proxyServerAdminThreadNumStr));
        }

        String proxyServerBatchBlockQSizeStr = configurationWraper.getProp(ConfKeys.KEY_PROXY_BATCHMSG_BLOCKQ_SIZE);
        if (StringUtils.isNotEmpty(proxyServerBatchBlockQSizeStr) && StringUtils.isNumeric(proxyServerBatchBlockQSizeStr)) {
            proxyServerBatchBlockQSize = Integer.valueOf(StringUtils.deleteWhitespace(proxyServerBatchBlockQSizeStr));
        }

        String proxyServerSendMsgBlockQSizeStr = configurationWraper.getProp(ConfKeys.KEY_PROXY_SENDMSG_BLOCKQ_SIZE);
        if (StringUtils.isNotEmpty(proxyServerSendMsgBlockQSizeStr) && StringUtils.isNumeric(proxyServerSendMsgBlockQSizeStr)) {
            proxyServerSendMsgBlockQSize = Integer.valueOf(StringUtils.deleteWhitespace(proxyServerSendMsgBlockQSizeStr));
        }

        String proxyServerClientManageBlockQSizeStr = configurationWraper.getProp(ConfKeys.KEY_PROXY_CLIENTM_BLOCKQ_SIZE);
        if (StringUtils.isNotEmpty(proxyServerClientManageBlockQSizeStr) && StringUtils.isNumeric(proxyServerClientManageBlockQSizeStr)) {
            proxyServerClientManageBlockQSize = Integer.valueOf(StringUtils.deleteWhitespace(proxyServerClientManageBlockQSizeStr));
        }

        proxyUserName = StringUtils.trim(configurationWraper.getProp(ConfKeys.KEYS_PROXY_USERNAME));
        proxyPasswd = StringUtils.trim(configurationWraper.getProp(ConfKeys.KEYS_PROXY_PASSWORD));

        String clientPubWindowStr = configurationWraper.getProp(ConfKeys.KEYS_PROXY_CLIENT_PUB_WINDOW);
        Preconditions.checkState(StringUtils.isNotEmpty(clientPubWindowStr) && StringUtils.isNumeric(clientPubWindowStr), String.format("%s error", ConfKeys.KEYS_PROXY_CLIENT_PUB_WINDOW));
        pubWindow = Integer.valueOf(clientPubWindowStr);

        String clientPollNamesrvIntevelStr = configurationWraper.getProp(ConfKeys.KEYS_PROXY_CLIENT_POLL_NAMESRV_INTERVEL);
        if (StringUtils.isNotEmpty(clientPollNamesrvIntevelStr)) {
            Preconditions.checkState(StringUtils.isNumeric(clientPollNamesrvIntevelStr), String.format("%s error", ConfKeys.KEYS_PROXY_CLIENT_POLL_NAMESRV_INTERVEL));
            pollNameServerInteval = Integer.valueOf(clientPollNamesrvIntevelStr);
        }

        String clientHeartbeatBrokerIntervalStr = configurationWraper.getProp(ConfKeys.KEYS_PROXY_CLIENT_HEARTBEAT_BROKER_INTERVEL);
        if (StringUtils.isNotEmpty(clientHeartbeatBrokerIntervalStr)) {
            Preconditions.checkState(StringUtils.isNumeric(clientHeartbeatBrokerIntervalStr), String.format("%s error", ConfKeys.KEYS_PROXY_CLIENT_HEARTBEAT_BROKER_INTERVEL));
            heartbeatBrokerInterval = Integer.valueOf(clientHeartbeatBrokerIntervalStr);
        }

        String proxyTcpGlobalSchedulerStr = configurationWraper.getProp(ConfKeys.KEYS_PROXY_SERVER_GLOBAL_SCHEDULER);
        Preconditions.checkState(StringUtils.isNotEmpty(proxyTcpGlobalSchedulerStr) && StringUtils.isNumeric(proxyTcpGlobalSchedulerStr), String.format("%s error", ConfKeys.KEYS_PROXY_SERVER_GLOBAL_SCHEDULER));
        proxyTcpGlobalScheduler = Integer.valueOf(StringUtils.deleteWhitespace(proxyTcpGlobalSchedulerStr));

        String namesrvAddr = configurationWraper.getProp(ConfKeys.KEYS_PROXY_NAMESRV_ADDRS);
        Preconditions.checkState(StringUtils.isNotEmpty(namesrvAddr), String.format("%s error", ConfKeys.KEYS_PROXY_NAMESRV_ADDRS));
        namesrvAddrs = StringUtils.deleteWhitespace(namesrvAddr);

        String proxyServerAdminPortStr = configurationWraper.getProp(ConfKeys.KEYS_PROXY_SERVER_ADMIN_HTTP_PORT);
        Preconditions.checkState(StringUtils.isNotEmpty(proxyServerAdminPortStr) && StringUtils.isNumeric(proxyServerAdminPortStr), String.format("%s error", ConfKeys.KEYS_PROXY_SERVER_ADMIN_HTTP_PORT));
        proxyServerAdminPort = Integer.valueOf(StringUtils.deleteWhitespace(proxyServerAdminPortStr));
    }
}

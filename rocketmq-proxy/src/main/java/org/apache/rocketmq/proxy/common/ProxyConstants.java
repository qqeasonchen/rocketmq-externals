package org.apache.rocketmq.proxy.common;

public class ProxyConstants {
    public static final String PROTOCOL_HTTP = "http";

    public static final String PROTOCOL_TCP = "tcp";

    public static final String BROADCAST_PREFIX = "broadcast-";

    public final static String CONSUMER_GROUP_NAME_PREFIX = "ConsumerGroup-";

    public final static String PRODUCER_GROUP_NAME_PREFIX = "ProducerGroup-";

    public static final String DEFAULT_CHARSET = "UTF-8";

    public static final String IP_PORT_SEPARATOR = ":";

    public static final String HTTP_PROTOCOL_PREFIX = "http://";

    public static final String PROXY_CONF_HOME = System.getProperty("confPath", System.getenv("confPath"));

    public static final String REQ_C2PROXY_TIMESTAMP = "req_c2proxy_timestamp";
    public static final String REQ_PROXY2BROKER_TIMESTAMP = "req_proxy2broker_timestamp";

    public static final Integer DEFAULT_MSG_TTL_MILLS = 14400000;

    public static final int DEFAULT_TIMEOUT_IN_MILLISECONDS = 3000;

    public static final int DEFAULT_FASTFAIL_TIMEOUT_IN_MILLISECONDS = 100;

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    public static final String BORN_TIMESTAMP = "BORN_TIME";
    public static final String STORE_TIMESTAMP = "STORE_TIME";
    public static final String TTL = "TTL";
    public static final String CONSUME_SEQ = "SEQ";
    public static final String TAG = "TAG";

    public static class Language {
        public static final String JAVA = "JAVA";
    }
}

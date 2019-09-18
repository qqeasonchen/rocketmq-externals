package org.apache.rocketmq.proxy.common;

public class ProtocolKey {
    public static final String REQUEST_CODE = "Code";
    public static final String LANGUAGE = "Language";
    public static final String VERSION = "Version";

    public static class ClientInstanceKey {
        public static final String REGION = "Region";
        public static final String SYS = "Sys";
        public static final String GROUPNAME = "GroupName";
        public static final String PID = "Pid";
        public static final String IP = "Ip";
        public static final String USERNAME = "Username";
        public static final String PASSWD = "Passwd";
    }

    public static class ProxyInstanceKey {
        public static final String PROXYCLUSTER = "ProxyCluster";
        public static final String PROXYIP = "ProxyIp";
        public static final String PROXYENV = "ProxyEnv";
        public static final String PROXYREGION = "ProxyRegion";
        public static final String PROXYIDC = "ProxyIdc";
        public static final String PROXYDCN = "ProxyDcn";
    }

    public static final String RETCODE = "retCode";
    public static final String RETMSG = "retMsg";
    public static final String RESTIME = "responseTime";
}

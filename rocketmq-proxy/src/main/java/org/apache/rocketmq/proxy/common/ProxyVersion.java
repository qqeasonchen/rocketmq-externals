package org.apache.rocketmq.proxy.common;

public class ProxyVersion {
    public static final String CURRENT_VERSION = Version.V1_0_0.name();

    public static String getCurrentVersionDesc() {
        return CURRENT_VERSION.replaceAll("V", "")
            .replaceAll("_", ".")
            .replaceAll("_SNAPSHOT", "-SNAPSHOT");
    }

    public enum Version {
        V1_0_0,
        V1_0_1,
        V1_1_0,
        V1_2_0,
        V1_3_0
    }
}

package org.apache.rocketmq.proxy.common;

public enum ProtocolVersion {
    V1("1.0"),
    V2("2.0");

    private String version;

    ProtocolVersion(String version) {
        this.version = version;
    }

    public static ProtocolVersion get(String version) {
        if (V1.version.equals(version)) {
            return V1;
        } else if (V2.version.equals(version)) {
            return V2;
        } else {
            return null;
        }
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public static boolean contains(String version) {
        boolean flag = false;
        for (ProtocolVersion itr : ProtocolVersion.values()) {
            if (itr.version.equals(version)) {
                flag = true;
                break;
            }
        }
        return flag;
    }
}

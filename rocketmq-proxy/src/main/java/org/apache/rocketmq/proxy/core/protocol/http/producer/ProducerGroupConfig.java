package org.apache.rocketmq.proxy.core.protocol.http.producer;

import java.util.Objects;

public class ProducerGroupConfig {
    private String groupName;

    public ProducerGroupConfig(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    @Override public String toString() {
        return "ProducerGroupConfig{" +
            "groupName='" + groupName + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ProducerGroupConfig that = (ProducerGroupConfig) o;
        return groupName == that.groupName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupName);
    }
}

package org.apache.rocketmq.proxy.utils;

import java.util.concurrent.ThreadLocalRandom;

public class ThreadUtil {
    public static void randomSleep(int min, int max) throws Exception {
        // nextInt is normally exclusive of the top value, so add 1 to make it inclusive
        int random = ThreadLocalRandom.current().nextInt(min, max + 1);
        Thread.sleep(random);

    }

    public static void randomSleep(int max) throws Exception {
        randomSleep(1, max);
    }

    public static long getPID() {
        String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
        if (processName != null && processName.length() > 0) {
            try {
                return Long.parseLong(processName.split("@")[0]);
            } catch (Exception e) {
                return 0;
            }
        }

        return 0;
    }
}

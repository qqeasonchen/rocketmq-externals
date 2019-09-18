package org.apache.rocketmq.proxy.configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.ThreadFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationWraper {
    public Logger logger = LoggerFactory.getLogger(this.getClass());

    private String file;

    private Properties properties = new Properties();

    private boolean reload = true;

    private ScheduledExecutorService configLoader = Executors
        .newSingleThreadScheduledExecutor(new ThreadFactoryImpl("proxy-configloader"));

    public ConfigurationWraper(String file, boolean reload) {
        this.file = file;
        this.reload = reload;
        init();
    }

    private void init() {
        load();
        if (this.reload) {
            configLoader.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    load();
                }
            }, 30 * 1000, 30 * 1000, TimeUnit.MILLISECONDS);
        }
    }

    private void load() {
        try {
            properties.load(new BufferedReader(new FileReader(
                new File(file))));
        } catch (IOException e) {
            logger.error("loading properties [{}] error", file, e);
        }
    }

    public String getProp(String key) {
        return StringUtils.isEmpty(key) ? null : properties.getProperty(key, null);
    }

}

package org.apache.rocketmq.proxy.boot;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.rocketmq.proxy.utils.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractRemotingServer {
    public Logger logger = LoggerFactory.getLogger(this.getClass());

    public EventLoopGroup bossGroup;

    public EventLoopGroup ioGroup;

    public EventLoopGroup workerGroup;

    public int port;

    private EventLoopGroup initBossGroup(String threadPrefix) {
        bossGroup = new NioEventLoopGroup(1, new ThreadFactory() {
            AtomicInteger count = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, threadPrefix + "-boss-" + count.incrementAndGet());
                t.setDaemon(true);
                return t;
            }
        });

        return bossGroup;
    }

    private EventLoopGroup initIOGroup(String threadPrefix) {
        ioGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors(), new ThreadFactory() {
            AtomicInteger count = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, threadPrefix + "-io-" + count.incrementAndGet());
                return t;
            }
        });
        return ioGroup;
    }

    private EventLoopGroup initWokerGroup(String threadPrefix) {
        workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors(), new ThreadFactory() {
            AtomicInteger count = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, threadPrefix + "-worker-" + count.incrementAndGet());
                return t;
            }
        });
        return workerGroup;
    }

    public void init(String threadPrefix) throws Exception {
        initBossGroup(threadPrefix);
        initIOGroup(threadPrefix);
        initWokerGroup(threadPrefix);
    }

    public void shutdown() throws Exception {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
            logger.info("shutdown bossGroup");
        }

        ThreadUtil.randomSleep(30);

        if (ioGroup != null) {
            ioGroup.shutdownGracefully();
            logger.info("shutdown ioGroup");
        }

        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
            logger.info("shutdown workerGroup");
        }
    }

    public void start() throws Exception {

    }
}

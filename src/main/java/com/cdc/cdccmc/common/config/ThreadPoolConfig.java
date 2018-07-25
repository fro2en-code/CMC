package com.cdc.cdccmc.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;


public class ThreadPoolConfig {

    private static Integer corePoolSize = 5;
    private static Integer maximumPoolSize = 50;
    private static Long keepAliveTime = 10L;

    private static ThreadPoolExecutor threadPoolExecutor;

    public static ExecutorService getThreadPool()
    {
        if(null == threadPoolExecutor)
        {
            initThreadPool();
        }
        return threadPoolExecutor;
    }

    public static void initThreadPool()
    {
        threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                keepAliveTime, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
    }

}

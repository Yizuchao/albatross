package com.yogi.albatross.utils;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolUtils {
    private static ThreadPoolExecutor excutor=null;

    static {
        excutor=new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors()*2,
                Integer.MAX_VALUE,10, TimeUnit.SECONDS,new LinkedBlockingDeque<>(),new DefaultThreadFactory());
    }

    public static void execute(Runnable r){
        excutor.execute(r);
    }
    public static void execute(String threadName,Runnable r){
        excutor.execute(() ->{
            Thread.currentThread().setName(threadName);
            r.run();
        });
    }


    private static class DefaultThreadFactory implements ThreadFactory{
        private final String defaultPrefix;
        private final ThreadGroup group;
        private final AtomicInteger threadNumber=new AtomicInteger(NumberUtils.INTEGER_ONE);

        public DefaultThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            defaultPrefix="custompool-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    defaultPrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}

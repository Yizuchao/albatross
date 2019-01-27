package com.yogi.albatross.utils;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolUtils {
    private static ThreadPoolExecutor excutor=null;
    private static ScheduledThreadPoolExecutor deplayExcutor=null;
    private static int availableProcessors=Runtime.getRuntime().availableProcessors();

    static {
        excutor=new ThreadPoolExecutor(availableProcessors,
                availableProcessors*2,10, TimeUnit.SECONDS,new LinkedBlockingDeque<>(),
                new DefaultThreadFactory("publish-message-threadpool-"));
        deplayExcutor=new ScheduledThreadPoolExecutor(1,
                new DefaultThreadFactory("delay-message-threadpool-"));
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

    public static void deplayExcute(Runnable r,long delaySeconds){
        deplayExcutor.schedule(r,delaySeconds,TimeUnit.SECONDS);
    }

    private static class DefaultThreadFactory implements ThreadFactory{
        private final String defaultPrefix;
        private final ThreadGroup group;
        private final AtomicInteger threadNumber=new AtomicInteger(NumberUtils.INTEGER_ONE);

        public DefaultThreadFactory(String defaultPrefix) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            this.defaultPrefix=defaultPrefix;
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

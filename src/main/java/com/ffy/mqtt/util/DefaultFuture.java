package com.ffy.mqtt.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.ffy.mqtt.model.Message;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Data
@Slf4j
public class DefaultFuture implements Future {
    private static final Map<Long, DefaultFuture> FUTURES   = new ConcurrentHashMap<Long, DefaultFuture>();
    private final Lock lock = new ReentrantLock();
    private final Condition done = lock.newCondition();
    private  int                             timeout;
    private  long                            id;
    private final long                            start = System.currentTimeMillis();
    private  boolean                           cancel = false;
    public final static AtomicLong REQUEST_ID_GEN = new AtomicLong(0);
    private Message msg;
    public DefaultFuture(Long id, int timeout){
        this.id=id;
        this.timeout=timeout;
        FUTURES.put(id, this);
    }

    public DefaultFuture( int timeout){
        this.id=generateId();
        this.timeout=timeout;
        FUTURES.put(id, this);
    }

    public static Long  generateId(){
        Snowflake snowflake = IdUtil.createSnowflake(1, 1);
        long id = snowflake.nextId();
        return id;
    }
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        FUTURES.remove(id);
        this.  cancel=true;
        return true;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public boolean isDone() {
        return msg != null;
    }

    @SneakyThrows
    @Override
    public Message get() {
        return get(timeout,TimeUnit.SECONDS);
    }

    @Override
    public Message get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (timeout <= 0) {
             throw new RuntimeException("参数错误");
        }
        if (! isDone()) {
            long start = System.currentTimeMillis();
            lock.lock();
            try {
                while (! isDone()) {
                    done.await(timeout, TimeUnit.SECONDS);
                    if (isDone() || System.currentTimeMillis() - start > timeout) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
            if (! isDone()) {
                throw new RuntimeException("请求超时");
            }
        }
        return msg;
    }

    private long getStartTimestamp() {
        return start;
    }
    public static boolean contains(Long msgId){
        return FUTURES.containsKey(msgId);
    }

    public static void received( Message msg) {
            DefaultFuture future = FUTURES.remove(msg.getMessageId());
            if (future != null) {
                future.doReceived(msg);
            }

    }
    private void doReceived(Message res) {
        lock.lock();
        try {
            this.msg = res;
            if (done != null) {
                done.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    private static class RemotingInvocationTimeoutScan implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    for (DefaultFuture future : FUTURES.values()) {
                        if (future == null || future.isDone()) {
                            continue;
                        }
                        if (System.currentTimeMillis() - future.getStartTimestamp() > future.getTimeout()*1000) {
                            Message timeoutResponse = new Message();
                            timeoutResponse.setMessageId(future.getId());
                            timeoutResponse.setPlayLoad("超时");
                            DefaultFuture.received(timeoutResponse);
                        }
                    }
                    Thread.sleep(30);
                } catch (Throwable e) {
                    log.error("Exception when scan the timeout invocation of remoting.", e);
                }
            }
        }
    }

    static {
        Thread th = new Thread(new RemotingInvocationTimeoutScan(), "MqttResponseTimeoutScanTimer");
        th.setDaemon(true);
        th.start();
    }
}

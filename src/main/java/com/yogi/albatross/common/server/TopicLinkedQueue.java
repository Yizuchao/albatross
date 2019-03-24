package com.yogi.albatross.common.server;


import java.io.Serializable;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * topic消息阻塞队列实现
 *
 * @param <T>
 */
public class TopicLinkedQueue<T> implements Iterable<T>, Serializable {
    private static final long serialVersionUID = 1L;
    private final int NO_ILIMIT_CAPACITY = -1;
    private final long capacity;
    private final AtomicLong count = new AtomicLong(0);

    private final ReentrantLock putLock = new ReentrantLock();
    private final Condition notifyOffer = putLock.newCondition();

    private Node<T> head;
    private Node<T> tail;

    public TopicLinkedQueue(long capacity) {
        this.capacity = capacity;
    }

    public TopicLinkedQueue() {
        this.capacity = NO_ILIMIT_CAPACITY;//无容量限制
    }

    @Override
    public Iterator<T> iterator() {
        return new DefaultIterator();
    }

    private class Node<T> {
        private T t;
        private Node<T> next;

        public Node(T t) {
            this.t = t;
        }

        public Node<T> next() {
            return next;
        }

        public void next(Node<T> next) {
            this.next = next;
        }

        public T unWrap() {
            return t;
        }
    }

    public boolean offer(T t, TimeUnit unit, long timeout) throws InterruptedException {
        if (Objects.isNull(t)) {
            throw new NullPointerException("offer object must not null");
        }
        long c = -1;
        long nanosTime = unit.toNanos(timeout);
        putLock.lockInterruptibly();
        try {
            //若队列是满的，则等待指定时间
            while (capacity != NO_ILIMIT_CAPACITY && count.get() == capacity) {
                if (nanosTime <= 0) {
                    return false;
                }
                nanosTime = notifyOffer.awaitNanos(nanosTime);
            }
            enqueue(new Node<>(t));
            c = count.getAndIncrement();
            //唤醒其它offer线程
            if (c + 1 < capacity) {
                notifyOffer.signalAll();
            }
        } finally {
            putLock.unlock();
        }
        //若队列原本数量是0，则所有的take线程已经阻塞，去唤醒take线程。
        /*if (c == 0) {
            signalNotEmpty();
        }*/
        return true;
    }

    private void enqueue(Node<T> node) {
        if (Objects.isNull(head)) {
            head = node;
        }
        if (Objects.isNull(tail)) {
            tail = node;
        } else {
            node.next(tail);
            tail = node;
        }
    }

    private class DefaultIterator implements Iterator<T>, Serializable {
        private static final long serialVersionUID = 1L;

        private Node<T> current;
        private final AtomicLong itCount = new AtomicLong(0);

        public DefaultIterator() {
            this.current = head;
        }

        @Override
        public boolean hasNext() {
            if (itCount.get() == count.get()) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        public T next() {
            if (itCount.get() < count.get()) {
                Node<T> next = current.next();
                current = next;
                itCount.incrementAndGet();
                return next.unWrap();
            }
            return null;
        }
    }
}

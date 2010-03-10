package org.hydracache.concurrent;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public class SimpleResultFuture<T> implements Future<Collection<T>> {
    private final int expectedMessages;

    private AtomicBoolean cancelled;

    private ConcurrentLinkedQueue<T> responses = new ConcurrentLinkedQueue<T>();
    
    private CountDownLatch latch;

    /**
     * Default constructor
     */
    public SimpleResultFuture() {
        this(1);
    }

    /**
     * Constructor
     * 
     * @param expectedResults
     *            number of message this response should expect before it
     *            consider its job is done
     */
    public SimpleResultFuture(int expectedResults) {
        super();
        expectedMessages = expectedResults;
        this.latch = new CountDownLatch(expectedMessages);        
        this.cancelled = new AtomicBoolean(false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.Future#cancel(boolean)
     */
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        while(latch.getCount() > 0)
            latch.countDown();

        return cancelled.getAndSet(true);
    }

    /**
     * Add the given result to this future
     * 
     * @param result
     *            result to add
     */
    public void add(T result) {
        if (isDone())
            return;

        responses.add(result);
        
        latch.countDown();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.Future#get()
     */
    @Override
    public Collection<T> get() throws InterruptedException,
            ExecutionException {
        return responses;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.Future#get(long, java.util.concurrent.TimeUnit)
     */
    @Override
    public synchronized Collection<T> get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        latch.await(timeout, unit);

        return responses;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.Future#isCancelled()
     */
    @Override
    public boolean isCancelled() {
        return cancelled.get();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.Future#isDone()
     */
    @Override
    public boolean isDone() {
        if (isCancelled())
            return true;

        return latch.getCount() == 0;
    }
}
/*
 * Copyright 2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hydracache.concurrent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.time.StopWatch;
import org.junit.After;
import org.junit.Test;

/**
 * @author nzhu
 * 
 */
public class SimpleResultFutureTest {

    private static final int TIMEOUT = 200;

    private Collection<Object> results;

    @After
    public void tearDown() {
        results = null;
    }

    @Test
    public void shouldGetResultsAfterAdd() throws Exception {
        Object msg = new Object();

        SimpleResultFuture<Object> responses = new SimpleResultFuture<Object>();

        responses.add(msg);

        assertNotNull(responses.get());
        assertTrue("Responses should contain the expected msg", responses.get()
                .contains(msg));
    }

    @Test
    public void shouldGetBeAbleToTimeout() throws Exception {
        SimpleResultFuture<Object> responses = new SimpleResultFuture<Object>();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        results = responses.get(TIMEOUT, TimeUnit.MILLISECONDS);

        stopWatch.stop();

        assertTrue("Timeout period is too short", stopWatch.getTime() > TIMEOUT);

        assertNotNull("Results should not be null", results);
        assertTrue("Results should be empty", results.isEmpty());
    }

    @Test
    public void shouldGetResultsAfterAddConcurrently() throws Exception {
        Object result = new Object();

        final SimpleResultFuture<Object> future = new SimpleResultFuture<Object>(
                3);

        concurrentGet(future);
        concurrentAdd(future);
        concurrentAdd(future);
        future.add(result);
        
        Thread.sleep(100);

        assertTrue("Future should be done", future.isDone());
        assertNotNull(results);
        assertTrue("Responses should contain the expected result", results
                .contains(result));
    }

    private void concurrentAdd(final SimpleResultFuture<Object> future) {
        new Thread() {
            @Override
            public void run() {
                try {
                    future.add(new Object());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void concurrentGet(final SimpleResultFuture<Object> future) {
        new Thread() {
            @Override
            public void run() {
                try {
                    StopWatch stopWatch = new StopWatch();
                    stopWatch.start();
                    results = future.get(TIMEOUT, TimeUnit.MILLISECONDS);
                    stopWatch.stop();
                    System.out.println("Took [" + stopWatch.getTime()
                            + "]ms to get concurrently");

                    assertTrue("Get took too long in a ideal case", stopWatch
                            .getTime() < TIMEOUT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Test
    public void shouldBeAbleToDetectIsDone() {
        SimpleResultFuture<Object> responses = new SimpleResultFuture<Object>(
                3);

        responses.add(new Object());
        assertFalse(responses.isDone());
        responses.add(new Object());
        assertFalse(responses.isDone());
        responses.add(new Object());

        assertTrue("Responses future should be done", responses.isDone());
    }

    @Test
    public void shouldBeAbleToCancel() throws Exception {
        SimpleResultFuture<Object> responses = new SimpleResultFuture<Object>(
                2);

        assertFalse(responses.isCancelled());

        responses.cancel(true);

        assertTrue("Responses should be done after cancellation", responses
                .isDone());
        assertTrue("Responses should be cancelled after cancellation",
                responses.isCancelled());

        responses.add(new Object());

        assertTrue("Response should be empty after cancellation", responses
                .get().isEmpty());
    }

}

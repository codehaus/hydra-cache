package org.hydracache.server.harmony.core;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.hydracache.protocol.control.message.HeartBeat;
import org.springframework.context.Lifecycle;

public class HealthMonitor implements Lifecycle {
    private static Logger log = Logger.getLogger(HealthMonitor.class);

    private Space space;

    private Executor singleThreadExecutor;

    private long interval;

    private TimeUnit intervalUnit;

    private AtomicBoolean running;

    public HealthMonitor(Space space, long interval, TimeUnit intervalUnit) {
        super();
        this.space = space;
        this.interval = interval;
        this.intervalUnit = intervalUnit;
        this.running = new AtomicBoolean(false);

        singleThreadExecutor = Executors
                .newSingleThreadExecutor(new HealthThreadFactory());
    }

    public void start() {
        running.set(true);

        singleThreadExecutor.execute(new Worker());
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }

    @Override
    public void stop() {
        running.lazySet(false);
    }

    private static final class HealthThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "HealthMonitor");
        }
    }

    private final class Worker implements Runnable {
        @Override
        public void run() {
            while (running.get()) {
                try {
                    if (space.isActive())
                        broadcastHeartbeat();

                    pause();
                } catch (InterruptedException iex) {
                    // ignore
                } catch (Exception e) {
                    log.error(
                            "Failed to broadcast heartbeat message in space: "
                                    + space, e);
                }
            }
        }

        private void pause() throws InterruptedException {
            Thread.sleep(intervalUnit.toMillis(interval));
        }

        private void broadcastHeartbeat() throws IOException {
            space.broadcast(new HeartBeat(space.getLocalNode()));
        }
    }

}

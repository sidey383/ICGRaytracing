package ru.sidey383.render.raytrace;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.sidey383.render.raytrace.controller.FineRaytraceController;
import ru.sidey383.render.raytrace.controller.NormalRaytraceController;
import ru.sidey383.render.raytrace.controller.RaytraceController;
import ru.sidey383.render.raytrace.controller.RoughRaytraceController;

import java.awt.image.BufferedImage;
import java.beans.IntrospectionException;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class RaytracingRender {

    public record RenderStatus(
            int completeCount,
            int totalCount,
            boolean isComplete,
            boolean isRunning,
            boolean isFailed,
            Throwable error
    ) {
    }

    @NotNull
    private final AtomicReference<Thread> controllerThread = new AtomicReference<>(null);
    @NotNull
    private final RaytraceController controller;
    private final int threadCount;
    @NotNull
    private final RaytraceConfiguration configuration;
    @NotNull
    private final AtomicBoolean isComplete = new AtomicBoolean(false);
    @NotNull
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    @NotNull
    private final AtomicReference<Throwable> error = new AtomicReference<>();
    @NotNull
    private final CountDownLatch latch;

    public RaytracingRender(RaytraceConfiguration configuration, int threadCount) {
        this.threadCount = threadCount;
        this.configuration = configuration;
        controller = switch (configuration.quality()) {
            case FINE ->
                    new FineRaytraceController(configuration.width(), configuration.height(), configuration.camera(), 2);
            case ROUGH ->
                    new RoughRaytraceController(configuration.width(), configuration.height(), configuration.camera(), 2);
            case NORMAL ->
                    new NormalRaytraceController(configuration.width(), configuration.height(), configuration.camera());
        };
        latch = new CountDownLatch(controller.total());
    }

    private static class CloseableScheduledThread implements Closeable, ThreadFactory {
        @Getter
        private final ExecutorService executor;

        private final ThreadGroup group = new ThreadGroup("RaytracingWorker");

        public CloseableScheduledThread(int tc) {
            this.executor = Executors.newFixedThreadPool(tc, this);
        }

        @Override
        public void close() {
            executor.shutdownNow();
        }

        @Override
        public Thread newThread(@NotNull Runnable r) {
            return new Thread(group, r);
        }
    }

    private synchronized void catchException(Thread t, Throwable e) {
        if (error.get() != null)
            return;
        error.set(e);
    }

    public void startRender(Consumer<RenderStatus> onComplete) {
        Thread monitor = new Thread(() -> {
            synchronized (this) {
                if (isRunning.get()) {
                    onComplete.accept(getStatus());
                    return;
                }
            }
            try {
                isRunning.set(true);
                List<RayTraceThread> threads = new ArrayList<>(this.threadCount);
                AtomicInteger counter = new AtomicInteger(0);
                for (int i = 0; i < this.threadCount; i++) {
                    threads.add(new RayTraceThread(controller, configuration, counter));
                }
                threads.forEach(t -> t.setUncaughtExceptionHandler(this::catchException));
                threads.forEach(Thread::start);
                boolean isCompleteThreads = true;
                for (RayTraceThread t : threads) {
                    try {
                        t.join();
                        isCompleteThreads = isCompleteThreads && t.isComplete();
                    } catch (InterruptedException e) {
                        if (error.get() == null)
                            error.set(e);
                        for (RayTraceThread ti : threads) {
                            ti.interrupt();
                        }
                    }
                }
                isComplete.set(true);
            } catch (Throwable t) {
                error.set(t);
            } finally {
                isRunning.set(false);
                onComplete.accept(getStatus());
            }
        });
        if (!controllerThread.compareAndSet(null, monitor))
            onComplete.accept(getStatus());
        else
            monitor.start();
    }

//    public void startRender(Consumer<RenderStatus> onComplete) {
//        Thread monitor = new Thread(() -> {
//            synchronized (this) {
//                if (isRunning.get()) {
//                    onComplete.accept(getStatus());
//                    return;
//                }
//            }
//            try (CloseableScheduledThread t = new CloseableScheduledThread(this.threadCount)) {
//                isRunning.set(true);
//                for (int y = 0; y < controller.totalY(); y++) {
//                    for (int x = 0; x < controller.totalX(); x++) {
//                        t.getExecutor().submit(new RayTraceTask(x, y, controller, configuration, latch));
//                        if (Thread.interrupted())
//                            throw new InterruptedException();
//                    }
//                }
//                latch.await();
//                isComplete.set(true);
//            } catch (Exception e) {
//                error.set(e);
//            } finally {
//                isRunning.set(false);
//                onComplete.accept(getStatus());
//            }
//
//        }, "RenderMonitor");
//        if (!controllerThread.compareAndSet(null, monitor))
//            onComplete.accept(getStatus());
//        else
//            monitor.start();
//    }

    public RenderStatus getStatus() {
        Throwable e = error.get();
        return new RenderStatus(
                controller.completePart(),
                controller.total(),
                isComplete.get(),
                isRunning.get(),
                e != null,
                e
        );
    }

    public void shutdown() {
        Thread t = controllerThread.get();
        if (t != null && !t.isInterrupted())
            t.interrupt();
    }

    public void drawImage(BufferedImage image, double gamma) {
        controller.apply(image, gamma);
    }

}

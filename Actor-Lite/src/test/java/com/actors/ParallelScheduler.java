package com.actors;

import org.junit.runners.model.RunnerScheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ParallelScheduler implements RunnerScheduler
{

    private ExecutorService threadPool = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors());

    @Override
    public void schedule(Runnable childStatement) {
        threadPool.submit(childStatement);
    }

    @Override
    public void finished() {
        try {
            threadPool.shutdown();
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Got interrupted", e);
        }
    }
}

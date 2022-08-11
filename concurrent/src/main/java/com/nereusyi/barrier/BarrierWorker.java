package com.nereusyi.barrier;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

public class BarrierWorker implements Callable<String> {

    private final CyclicBarrier barrier;

    public BarrierWorker(CyclicBarrier barrier) {
        this.barrier = barrier;
    }

    @Override
    public String call() throws Exception {
        System.out.println("called , wait for other thread...");
        try {
            barrier.await(60, TimeUnit.SECONDS);
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
        System.out.println("execute task...");
        return "ok";
    }
}

package com.nereusyi.latch;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class LatchWorker implements Callable<String> {

    private final CountDownLatch countDownLatch;

    public LatchWorker(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }


    @Override
    public String call() throws Exception {
        System.out.println("execute task...");
        countDownLatch.countDown();
        return "ok";
    }
}

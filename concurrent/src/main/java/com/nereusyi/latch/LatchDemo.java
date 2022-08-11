package com.nereusyi.latch;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LatchDemo {

    public static void main(String[] args) throws InterruptedException {
        int permit = 5;
        CountDownLatch countDownLatch = new CountDownLatch(permit);
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        try{
            for (int i = 0; i < permit; i++) {
                LatchWorker latchWorker = new LatchWorker(countDownLatch);
                executorService.submit(latchWorker);
            }

            countDownLatch.await();

            System.out.println("execute finished.");
        }finally {
            executorService.shutdown();
        }

    }

}

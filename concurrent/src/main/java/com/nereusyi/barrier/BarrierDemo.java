package com.nereusyi.barrier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class BarrierDemo {

    public static void main(String[] args) {
        int taskSize = 8;
        ExecutorService executorService = Executors.newFixedThreadPool(taskSize);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(taskSize, () -> System.out.println("barrier opening..."));
        List<Future<String>> result = new ArrayList<>();
        try {
            for (int i = 0; i < taskSize; i++) {
                Future<String> future = executorService.submit(new BarrierWorker(cyclicBarrier));
                result.add(future);
            }

            result.forEach( f -> {
                try {
                    f.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    if ( ! f.isCancelled() && f.isDone()) {
                        f.cancel(true);
                    }
                }
            });
        }finally {
            executorService.shutdown();
        }
    }
}

package com.nereusyi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.LongAdder;

public class LongAdderDemo {

    private static final LongAdder counter1 = new LongAdder();
    private static long counter2 = 0;

    public static void main(String[] args) {

        List<Future<?>> objects = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 100000; i++) {
            Future<?> future = executorService.submit(() -> {
                counter1.increment();
                counter2++;
            });
            objects.add(future);
        }
        objects.forEach(f -> {
            try {
                f.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println("count1=" + counter1.sumThenReset());
        System.out.println("count2=" + counter2);

        executorService.shutdown();
    }

}

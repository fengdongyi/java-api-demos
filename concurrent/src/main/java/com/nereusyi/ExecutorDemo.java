package com.nereusyi;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorDemo {

    public static void main(String[] args) {
        System.out.println("Hello world!");
        ExecutorService service = Executors.newSingleThreadExecutor();

        for (int i = 0; i < 10; i++) {
            int finalI = i;
            service.execute(() -> System.out.println("HelloWorld" + finalI));
        }

        service.shutdown();
    }

}
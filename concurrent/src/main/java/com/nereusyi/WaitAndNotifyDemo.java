package com.nereusyi;

import java.util.ArrayList;
import java.util.List;

/**
 * wait and notify for producer and consumer pattern
 */
public class WaitAndNotifyDemo {

    public static void main(String[] args) throws InterruptedException {

        WaitAndNotifyDemo waitAndNotify = new WaitAndNotifyDemo();
        Thread thread1 = new Thread(() -> {
            try {
                while (true)
                    waitAndNotify.produce();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                while (true)
                    waitAndNotify.consume();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        thread1.start();
        thread2.start();


        thread1.join();
        thread2.join();
    }

    private static final List<Integer> queue = new ArrayList<>();
    private static final Object LOCK = new Object();

    private static int i = 0;

    public void produce() throws InterruptedException {
        synchronized (LOCK) {
            while (isQueueFull()) {
                LOCK.wait();
            }
            queue.add(i++);
            LOCK.notifyAll();
        }
    }

    public void consume() throws InterruptedException {
        synchronized (LOCK) {
            while (queue.isEmpty()) {
                LOCK.wait();
            }
            Integer integer = queue.remove(0);
            System.out.println("consume " + integer);
            LOCK.notifyAll();
        }
    }

    public boolean isQueueFull(){
        return queue.size() >= 10;
    }
}

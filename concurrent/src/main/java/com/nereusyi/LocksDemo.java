package com.nereusyi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ProducerConsumerWithLocksAndSemaphores
 */
public class LocksDemo {

    public static void main(String[] args) throws InterruptedException {
        LocksDemo locks = new LocksDemo();

        Runnable producer = () -> {
            try {
                while (true)
                    locks.produce();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
        Runnable consumer = () -> {
            try {
                while (true)
                    locks.consume();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        Thread thread1 = new Thread(producer);

        Thread thread2 = new Thread(consumer);
        Thread thread3 = new Thread(consumer);

        thread1.start();
        thread2.start();
        thread3.start();

        thread1.join();
        thread2.join();
        thread3.join();
    }

    private static final Lock lock = new ReentrantLock();
    private static final Condition notFull = lock.newCondition();
    private static final Condition notEmpty = lock.newCondition();

    private static final List<Integer> queue = new ArrayList<>();

    private static int i = 0;


    public void produce() throws InterruptedException {
        try{
            lock.lock();
            while (isQueueFull()) {
                notFull.await();
            }
            queue.add(i++);
            notEmpty.signalAll();
        }finally {
            lock.unlock();
        }
    }

    public void consume() throws InterruptedException {
        try{
            lock.lock();
            while (queue.isEmpty()) {
                notEmpty.await();
            }
            Integer integer = queue.remove(0);
            System.out.println("consume " + integer);
            notFull.signalAll();
        }finally {
            lock.unlock();
        }
    }

    public boolean isQueueFull(){
        return queue.size() >= 10;
    }
}

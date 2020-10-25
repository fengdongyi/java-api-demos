package com.nereusyi.demos.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @author Nereus Yi
 */
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class JmhDemo {

    @State(Scope.Benchmark)
    public static class InputState {

        @Setup(Level.Trial)
        public void setup(){
            System.out.println("setup");
        }

        int[] input = IntStream.range(1, 10000).toArray();

        @TearDown(Level.Trial)
        public void tearDown(){
            System.out.println("tearDown");
        }
    }

    @Benchmark
    public int[] testMethod(InputState inputState) {
        int[] input = inputState.input;
        int inputLength = input.length;
        int temp;
        boolean is_sorted;

        for (int i = 0; i < inputLength; i++) {
            is_sorted = true;
            for (int j = 1; j < (inputLength - i); j++) {
                if (input[j - 1] < input[j]) {
                    temp = input[j - 1];
                    input[j - 1] = input[j];
                    input[j] = temp;
                    is_sorted = false;
                }
            }
            if (is_sorted) break;
        }
        return input;
    }

    @Benchmark
    public int[] testMethod2(InputState inputState) {
        int[] input = inputState.input;
        for (int i = 1; i < input.length; i++) {
            int key = input[i];
            int j = i - 1;
            while (j >= 0 && input[j] < key) {
                input[j + 1] = input[j];
                j = j - 1;
            }
            input[j + 1] = key;
        }
        return input;
    }

    @State(Scope.Benchmark)
    public static class InputState2 {
        int a = 1 ;
        int b = 2;
    }

    @Benchmark
    public void testMethod3(InputState2 inputState2,Blackhole blackhole) {
        int sum = inputState2.a + inputState2.b;
        blackhole.consume(sum);
    }


    public static void main(String[] args) throws IOException {
        org.openjdk.jmh.Main.main(args);
    }
}

package com.yb.Hikari;

public class VolitailTest {
    public volatile int j = 0;
    public void addJ(){
        j++;
    }



    public static void main(String[] args) throws InterruptedException {
        VolitailTest test = new VolitailTest();
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                test.addJ();
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                test.addJ();
            }
        });
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        System.out.println(test.j);
    }
}


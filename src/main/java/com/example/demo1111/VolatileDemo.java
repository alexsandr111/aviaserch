package com.example.demo1111;

public class VolatileDemo {
    private static volatile int number = 0;

    public static void main(String[] args) {
        new Thread(() -> {
            number = 42;
            System.out.println("Число установлено: " + number);
        }).start();

        new Thread(() -> {
            while (number == 0) {
            }
            System.out.println("Число прочитано: " + number);
        }).start();
    }
}



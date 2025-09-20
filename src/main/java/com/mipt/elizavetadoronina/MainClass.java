package com.mipt.elizavetadoronina;

public class MainClass {
    private int someInt;
    private String someStr;
    protected static double someDoub;
    public final long someLong = 1L;

    public static void main(String[] args) {
        for (int i = 0; i < 15; i++) {
            System.out.println("Iter: " + i);
        }
    }
}

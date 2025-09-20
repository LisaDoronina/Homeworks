package com.mipt.elizavetadoronina;

public abstract class WorkingMan {
    public abstract void work(int workTime);

    public boolean goHime(String word1, String word2) {
        return word1.equals(word2);
    }
}
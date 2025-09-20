package com.mipt.elizavetadoronina.model;

public class Human {
    private String firstName;
    private String lastName;
    private int age;
    boolean working;

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setWorking(boolean working) {
        this.working = working;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getAge() {
        return age;
    }

    public boolean working() {
        return working;
    }
}
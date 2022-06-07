package com.nereusyi.demos.reflection;

public class Person {

    public String job;

    private String name;
    private String location;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void print(){
        System.out.println("Person job is " + job);
    }
}

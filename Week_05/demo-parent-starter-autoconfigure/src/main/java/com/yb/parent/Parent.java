package com.yb.parent;

public class Parent {
    private String name;
    public Parent() {
    }

    public Parent(String name) {
        this.name = name;
        System.out.println("parent is created, name = " + name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Parent{" +
                "name='" + name + '\'' +
                '}';
    }
}

package com.yb.jdkProxy;

/***
 * 被代理类
 *
 * @auther yb
 * @date 2020/11/13 20:02
 */
public class Student implements Person{
    private String name;

    public Student(String name) {
        this.name = name;
    }

    @Override
    public void eat(String food) {
        System.out.println("Student eat=====" + food);
    }

    @Override
    public void run() {
        System.out.println("Student run=====");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                '}';
    }
}

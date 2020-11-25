package com.yb.crud;

public class Outer {
    private int x = 100; // 成员变量可以被修改

    public AnnoInner getAnnoInner(){
        int y = 50; // 局部变量不能被修改
        AnnoInner annoInner = null; // 编译报错
        return annoInner;

    }



    public static void main(String[] args) {
        Outer outer = new Outer();
        outer.getAnnoInner().addXYZ();
        System.out.println("outter x = " + outer.getX());
    }

    public int getX() {
        return x;
    }
}

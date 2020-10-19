package com.yb;

import java.util.ArrayList;

public class Hello {
    private static ArrayList<Student> students;

    private static final int MIN_AGE = 18;

    static {
        students = new ArrayList<Student>();
        students.add(new Student(17));
        students.add(new Student(17));
        students.add(new Student(18));
    }

    public int checkAge (){
        int young_num = 0;
        int man_num = 0;
        for (Student student : students) {
            int age = student.getAge();
            if (age < MIN_AGE){
                young_num++;
            } else {
                man_num =man_num + 1;
            }
        }

        int diff = man_num - young_num;

        double man_rate = (man_num * 1d)/students.size();

        return young_num;
    }

}

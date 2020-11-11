package com.yb.homework;

import java.util.concurrent.*;

/***
 * main方法中新起线程，获取返回值后结束main
 *
 * @auther yb
 * @date 2020/11/11 12:53
 */
public class Demo1 {
    public static void main(String[] args) throws Exception{
        Student student = new Student(5L); // 初始化

        //方式1 使用线程池submit的返回值Future获取子线程结果
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 10, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(8));
        Future<Student> future1 = executor.submit(new TaskOfRunnable(student), student);
        Student r1 = future1.get(); // 阻塞main线程，直到子线程执行完毕
        System.out.println(student == r1);
        System.out.println("executor result = " + r1.getTime()); // 与run时相等

        //方式2 非线程池runnable,join阻塞获取子线程结果
        Thread t1 = new Thread(new TaskOfRunnable(student));
        t1.start();
        t1.join(); // 阻塞main线程，直到子线程执行完毕
        System.out.println("t1 result = " + student.getTime()); // 与run时相等

        // 方式3 使用FutureTask工具类获取子线程结果
        FutureTask<Long> ft = new FutureTask<>(() -> {
            Long result = System.currentTimeMillis();
            System.out.println("FutureTask run " + result);
            return result;
        });
        Thread t2 = new Thread(ft);
        t2.start();
        Long ftResult = ft.get();
        System.out.println("ft result = " + ftResult);

    }

    // runnable
    static class TaskOfRunnable implements Runnable{
        private Student result;

        public TaskOfRunnable(Student result) {
            this.result = result;
        }

        @Override
        public void run() {
            result.setTime(System.currentTimeMillis());
            System.out.println("TaskOfRunnable run == " + result.getTime());
        }
    }

    // callable
    static class TaskOfCallable implements Callable<Student>{
        private Student result;

        public TaskOfCallable(Student result) {
            this.result = result;
        }

        @Override
        public Student call() throws Exception {
            result.setTime(System.currentTimeMillis());
            System.out.println("TaskOfCallable run == " + result.getTime());
            return result;
        }
    }

    // FutureTask
    static class TaskOfFutreTask extends FutureTask<Student> {

        public TaskOfFutreTask(Callable<Student> callable) {
            super(callable);
        }

    }

    static class Student{
        private long time;

        public Student(long time) {
            this.time = time;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }
    }
}

package com.yb.batch;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import static com.yb.batch.ConnectionInfo.*;

/***
 * 使用PrepareStatement与多线程, 关闭自动提交batch,每1000条commit一次
 *
 *
 * @auther yb
 * @date 2020/11/30 10:03
 *
 * 插入结果：
st_time == 1606716111435
st_time == 1606716111434
st_time == 1606716111435
st_time == 1606716111446
st_time == 1606716111457
st_time == 1606716111469
st_time == 1606716111493
st_time == 1606716111504
st_time == 1606716111515
st_time == 1606716111543
pool-1-thread-1,commit
pool-1-thread-9,commit
pool-1-thread-6,commit
...
pool-1-thread-7,commit
pool-1-thread-8,commit
10w inserted! use 1606716164983pool-1-thread-8
pool-1-thread-8,over===============
pool-1-thread-2,commit
10w inserted! use 1606716165050pool-1-thread-2
pool-1-thread-2,over===============
pool-1-thread-6,commit
10w inserted! use 1606716165083pool-1-thread-6
pool-1-thread-6,over===============
pool-1-thread-7,commit
pool-1-thread-7,commit
10w inserted! use 1606716165342pool-1-thread-7
pool-1-thread-7,over===============
 100w累计耗时=1606716165342-1606716111434=53908 ms = 53 s
 对比之前的235s，快了将近5倍。
 */

public class PSBatchWithoutAutoCommitBy1000AndConcurrent {

    private static AtomicLong id = new AtomicLong(0);
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(10, 20, 500L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10000));
        PSBatchWithoutAutoCommitBy1000AndConcurrent demo = new PSBatchWithoutAutoCommitBy1000AndConcurrent();
        // 创建10个task，并行执行insert
        for (int i = 1; i <= 10; i++) {
            poolExecutor.execute(() -> {
                try {
                    demo.insert();
                } catch (SQLException e) {
                }
            });
        }
    }

    // 每个线程使用一个连接，执行10w条插入
    private void insert() throws SQLException {
        String threadName = Thread.currentThread().getName();
        Connection connection = DataSourceFactory.getInstance().getConnection();
        connection.setAutoCommit(false); // 关闭自动提交
        // 获取 ps
        String insert_sql = "insert into t_order VALUES (?,?,?,?,?,?,?)";
        PreparedStatement ps = connection.prepareStatement(insert_sql);
        int batch_insert_num = 0;
        int num = 0; // 批次数
        long st_time = System.currentTimeMillis();
        long end_time = 0L;
        System.out.println("st_time == " + st_time);
        // 设置公共参数
        ps.setLong(2, 111);
        ps.setLong(3, 123);
        ps.setLong(4, 1);
        ps.setString(5, "1");
        for (int i = 1; i <= 100000; i++) {
            ps.setLong(1, id.incrementAndGet());
            ps.setLong(6, System.currentTimeMillis());
            ps.setLong(7, System.currentTimeMillis());
            ps.addBatch();
            // 每1000条提交一次
            if (i % 1000 == 0){
                ps.executeBatch();
                connection.commit();
                System.out.println(threadName + ",commit");
            }
            if (++batch_insert_num == 10000){
                end_time = System.currentTimeMillis();
                System.out.println(++num + "w inserted! use " + end_time + threadName);
                batch_insert_num = 0; // 重置
            }
        }
        System.out.println(threadName + ",over===============");
        ps.close();
        connection.close();
    }
}

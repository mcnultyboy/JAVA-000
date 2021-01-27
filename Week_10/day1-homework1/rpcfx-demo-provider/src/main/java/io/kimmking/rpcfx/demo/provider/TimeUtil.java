package io.kimmking.rpcfx.demo.provider;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TimeUtil {
    public static void main(String[] args) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        long time = System.currentTimeMillis();
        System.out.println("before time=" + time);
        Timestamp timestamp = new Timestamp(time);
        Date date = new Date(timestamp.getTime());
        String format = sdf.format(date);
        System.out.println("format=" + format);

        java.util.Date dateAfter = sdf.parse(format);
        System.out.println("after time="+dateAfter.getTime());

        List<Integer> list =new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        System.out.println(list.get(list.size()-1));


    }
}

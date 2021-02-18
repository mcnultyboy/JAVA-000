package com.yb;

import java.util.Arrays;

public class ConsistentHashDemo {

    public static void main(String[] args) {
        test1();
    }

    public static void test1(){

        ConsistentHash<String> consistentHash = new ConsistentHash<String>(
                new HashFunction(),
                2,
                Arrays.asList("clu1", "clu2", "clu3", "clu4"));
        String cluNum = consistentHash.get("111");
        System.out.println(cluNum);
        consistentHash.remove("clu4");
        cluNum = consistentHash.get("111");
        System.out.println(cluNum);

    }
}

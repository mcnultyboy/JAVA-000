package com.yb;

import com.yb.HashFunction;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 实现一致性hash计算：实现保存route节点方法、实现根据cstID key来查找存储的节点方法
 * @param <T> ： 数据库节点对象（此处使用集群地址）
 */
public class ConsistentHash<T> {
    //hash函数接口，调用该接口的hash(key)方法，计算hash值
    private final HashFunction hashFunction;

    //每个机器节点，关联的虚拟节点个数
    private final int numberOfReplicas;

    //环形虚拟节点
    private final SortedMap<Long, T> circle = new TreeMap<Long, T>();

    /**
     * @param hashFunction：hash 函数接口
     * @param numberOfReplicas；每个机器节点关联的虚拟节点个数
     * @param nodes: 真实机器节点
     */
    public ConsistentHash(HashFunction hashFunction, int numberOfReplicas, Collection<T> nodes) {
        this.hashFunction = hashFunction;
        this.numberOfReplicas = numberOfReplicas;
        //遍历真实节点，生成对应的虚拟节点
        for (T node : nodes) {
            add(node);
        }
    }

    /**
     * 增加真实机器节点
     * 由真实节点，计算生成虚拟节点
     * @param node
     */
    public void add(T node) {
        for (int i = 0; i < this.numberOfReplicas; i++) {
            long hashcode = this.hashFunction.hash(node.toString() + "-" + i);
            circle.put(hashcode, node);
        }
    }

    /**
     * 删除真实机器节点
     *
     * @param node
     */
    public void remove(T node) {
        for (int i = 0; i < this.numberOfReplicas; i++) {
            long hashcode = this.hashFunction.hash(node.toString() + "-" + i);
            circle.remove(hashcode);
        }
    }

    /**
     *
     * 根据数据的key，计算hash值，然后从虚拟节点中，查询取得真实机器节点对象
     * @param key
     * @return
     */
    public T get(String key) {
        if (circle.isEmpty()) {
            return null;
        }
        long hash = hashFunction.hash(key);
        if (!circle.containsKey(hash)) {
            SortedMap<Long, T> tailMap = circle.tailMap(hash);// 沿环的顺时针找到一个虚拟节点
            hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
        }
        return circle.get(hash); // 返回该虚拟节点对应的真实机器节点的信息
    }
}
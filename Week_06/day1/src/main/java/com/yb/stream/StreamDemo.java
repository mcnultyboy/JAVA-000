package com.yb.stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StreamDemo {

    public static void main(String[] args) {
        /*List<Integer> list = Arrays.asList(1, 2, 2, 3, 4, 5, 6, 5, 4);
        List<Integer> collect = list.stream().filter((i) -> i != 3).collect(Collectors.toList());
        print(collect); // 1,2,4,5,6
        print(list); // 1,2,3,4,5,6 不会更改源数据的内容
        // 去重
        collect = list.stream().distinct().collect(Collectors.toList());
        print(collect);
        // 去头3个
        collect = list.stream().skip(3).collect(Collectors.toList());
        print(collect);
        // 去尾，只保留头三个元素
        collect = list.stream().limit(3).collect(Collectors.toList());
        print(collect);
        // map，创建一个新的集合
        collect = list.stream().map(i -> i + 1).collect(Collectors.toList());
        print(collect);
        // sort
        collect = list.stream().sorted().collect(Collectors.toList());
        print(collect);
        // reduct 进行累加
        Optional<Integer> reduce = list.stream().reduce((a, b) -> a + b);
        System.out.println(reduce.get());
        // 切换成并行的方式
        reduce = list.parallelStream().reduce((a, b) -> a + b);
        System.out.println(reduce);
        // 匹配,返回boolean值，表示当前集合是否有满足要求的元素
        boolean b = list.stream().anyMatch(i -> i > 100);
        System.out.println(b);
        Optional<Integer> first = list.stream().findFirst();
        System.out.println(first.get());
        // 取最大值
        Optional<Integer> max = list.stream().max((a, c) -> a - c);
        System.out.println(max);*/
        ArrayList<Person> list = new ArrayList<>();
        list.add(new Person("zhangsan", 18));
        list.add(new Person("lisi", 20));
        list.add(new Person("wangwu", 21));
        list.add(new Person("wangwu", null));
        queryPerson(list).forEach(var -> System.out.println(var));


    }

    private static void print(List<Integer> list) {
        System.out.println(String.join(",",list.stream().map(i -> i.toString()).collect(Collectors.toList()).toArray(new String[]{})));
    }

    /***
     * 返回年龄大于20岁人员姓名
     *
     * @param list
     */
    public static List<String> queryPerson(List<Person> list){
        List<String> names = new ArrayList<>();
        if (list == null || list.size() == 0){ // 非空校验
            return names;
        }
        names = list.stream().filter(person -> {
                Integer age = person.getAge();
                return age != null && age > 20;}) // 筛选
            .map(person -> (person.getName()))
            .distinct() // 去重
            .collect(Collectors.toList());
        return names;
    }

}

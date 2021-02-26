package com.yb;

public class Test1 {
    public static void main(String[] args) {
        boolean flag = checkNum(1234);
        System.out.println(flag);
    }

    public static boolean checkNum(int input){
        boolean flag = true;
        String i = input + "";
        // 如果只有1位，则直接为true
        int length = i.length();
        if (length == 1) {
            return true;
        }
        //奇数长度校验
        if (length % 2 != 0) {
            int midIndex = (length - 1)/2;
            String preStr = i.substring(0, midIndex);
            String sufStr = i.substring(midIndex+1);
            char[] preChar = preStr.toCharArray();
            char[] sufChar = sufStr.toCharArray();
            for (int j = 0; j < preChar.length; j++) {
                char p = preChar[j];
                char s = sufChar[sufChar.length - j];
                if (p != s){
                    flag = false;
                    break;
                }
            }
            // 偶数长度校验
        } else {

        }
        return flag;
    }
}

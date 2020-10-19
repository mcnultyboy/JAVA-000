package com.yb;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 加载经过处理的class文件的classloader
 * 应用场景：对class进行加密保护
 *
 * @auther AQ
 * @date 2020/10/18 17:24
 */
public class HelloClassLoader extends ClassLoader {


    public static void main(String[] args) throws Exception{
        String className = "Hello";
        String classPath = HelloClassLoader.class.getResource("/Hello.xlass").getPath();
        System.out.println(classPath);

        Class<?> helloClass = new HelloClassLoader().findClass(className, classPath);
        String methodName = "hello";
        Method helloMethod = helloClass.getDeclaredMethod(methodName); // 获取hello函数对象
        helloMethod.setAccessible(true);
        Object hello = helloClass.newInstance(); // 获取实例对象
        helloMethod.invoke(hello);  // 调用hello方法成功
    }

    /**
     * 根据指定的className和classPtah获取Class对象
     *
     * @param className className
     * @param classPath class文件目录
     * @return java.lang.Class<?>
     * @auther AQ
     * @date 2020/10/18 17:41
     */
    protected Class<?> findClass(String className, String classPath) throws ClassNotFoundException {
        byte[] classBytes = getBytesByClassPath(classPath);
        return super.defineClass(className, classBytes, 0, classBytes.length);
    }

    /**
     * 获取指定目录class文件的字节
     *
     * @param classPath class文件目录
     * @return byte[] 解密后的字节数组
     * @auther AQ
     * @date 2020/10/18 17:29
     */
    private byte[] getBytesByClassPath(String classPath){
        FileInputStream input = null;
        ByteArrayOutputStream output = null;
        try {
            input = new FileInputStream(new File(classPath)); // class文件输入流
            output = new ByteArrayOutputStream();

            byte[] buffer = new byte[1];
            byte[] temp = new byte[1];
            int len = 0;
            while ((len = input.read(buffer)) != -1) {
                temp[0] = (byte)((byte)255 - buffer[0]); // 计算class的初始数据
                output.write(temp, 0, len);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return output.toByteArray();
    }
}

一、编程语言
高级语言--人可以看懂的语言。
java，面向对象，GC 有jvm的，有运行时、跨平台的语言。
1.gc 不仅内存回收，更重要的是内存申请，更准确的名字是内存管理器。
c语言，没有gc，需要手动申请和释放内存。

2.java跨平台，可能运行在主流的平台上。写代码时不用关心，将源码编译成二进制的class，由平台的运行时和jvm执行(jdk来解决跨平台的问题)。

3.java的生态完整。有很多的插件，遇到的问题也有对应的解决办法。

4.java c++ Rust 在内存上的区别
c/c++ 完全由人控制，很自由，但是需要小心内存泄漏。
java 完全由gc控制，写起来自由，内存有时有问题，需要jvm调优。
Rust，写代码不自由，需要按照其规则来进行内存管理。

2.字节码技术

1.简单编译
1)javac 编译工具
javac demo/jvm0104/HelloByteCode.java 使用 javac 编译 ，或者在 IDEA或者Eclipse等集成开发工具自动编译，基本上是等效 的。只要能找到对应的class即可。 
javac 不指定 ‐d 参数编译后生成的 .class 文件默认和源代码在同一个目录。
注意: javac 工具默认开启了优化功能, 生成的字节码中没有局部变量表 (LocalVariableTable)，相当于局部变量名称被擦除。如果需要这些调试信息, 在编 译时请加上 ‐g 选项。
JDK自带工具的详细用法, 请使用: javac ‐help 或者 javap ‐help 来查看。

2)javap 反编译工具
然后使用 javap 工具来执行反编译, 获取字节码清单： 1 javap ‐c demo.jvm0104.HelloByteCode 2 # 或者: 3 javap ‐c demo/jvm0104/HelloByteCode 4 javap ‐c demo/jvm0104/HelloByteCode.class 
javap 还是比较聪明的, 使用包名或者相对路径都可以反编译成功。
如果在class文件所在目录执行命令，则无需指定demo.jvm0104 or demo/jvm0104
javap -c 中，-c表示反编译。
不带 -g
Compiled from "HelloByteCode.java"
class demo.jvm01.HelloByteCode {
  demo.jvm01.HelloByteCode();
    Code:
       0: aload_0
       1: invokespecial #1                  // Method java/lang/Object."<init>":()V
       4: return

  public static void main(java.lang.String[]);
    Code:
       0: new           #2                  // class demo/jvm01/HelloByteCode
       3: dup
       4: invokespecial #3                  // Method "<init>":()V
       7: astore_1
       8: return
}

2查看class文件中的常量池信息

常量池指的是运行时常量池
javap -c -verbose xxx  -verbose表示输出附加信息

Classfile /E:/demo/jvm01/HelloByteCode.class
  Last modified 2020-10-14; size 437 bytes // 编译时间
  MD5 checksum fcaea6ccbe093872447376114fa3deb2 // MD5校验和
  Compiled from "HelloByteCode.java"
class demo.jvm01.HelloByteCode
  minor version: 0
  major version: 52 // 版本信息，52表示java8，每上升一个版本数字加1
  flags: ACC_SUPER // 历史原有，修正invokespecial指令调用super类方法问题
Constant pool: // 常量池
// #1 常量编号，其他地方可以引用；= 分隔符；
// Methodref 表示该常量指向的是一个方法类指向的是#4,方法签名指向#19，
// 即java/lang/Object.<init>:()V
// 
   #1 = Methodref          #4.#19         // java/lang/Object."<init>":()V
   #2 = Class              #20            // demo/jvm01/HelloByteCode
   #3 = Methodref          #2.#19         // demo/jvm01/HelloByteCode."<init>":()V
   #4 = Class              #21            // java/lang/Object
   #5 = Utf8               <init>
   #6 = Utf8               ()V
   #7 = Utf8               Code
   #8 = Utf8               LineNumberTable
   #9 = Utf8               LocalVariableTable
  #10 = Utf8               this
  #11 = Utf8               Ldemo/jvm01/HelloByteCode;
  #12 = Utf8               main
  #13 = Utf8               ([Ljava/lang/String;)V
  #14 = Utf8               args
  #15 = Utf8               [Ljava/lang/String;
  #16 = Utf8               obj
  #17 = Utf8               SourceFile
  #18 = Utf8               HelloByteCode.java
  #19 = NameAndType        #5:#6          // "<init>":()V
  #20 = Utf8               demo/jvm01/HelloByteCode
  #21 = Utf8               java/lang/Object
{
  demo.jvm01.HelloByteCode();
    descriptor: ()V
    flags:
    Code:
      stack=1, locals=1, args_size=1
         0: aload_0
         1: invokespecial #1                  // Method java/lang/Object."<init>":()V
         4: return
      LineNumberTable:
        line 3: 0
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       5     0  this   Ldemo/jvm01/HelloByteCode;

  public static void main(java.lang.String[]);
    descriptor: ([Ljava/lang/String;)V 
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=2, locals=2, args_size=1
         0: new           #2                  // class demo/jvm01/HelloByteCode
         3: dup
         4: invokespecial #3                  // Method "<init>":()V
         7: astore_1
         8: return
      LineNumberTable:
        line 7: 0
        line 8: 8
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       9     0  args   [Ljava/lang/String;
            8       1     1   obj   Ldemo/jvm01/HelloByteCode;
}

3.查看方法信息
main方法的更多信息在verbose中显示出来：
descriptor: ([Ljava/lang/String;)V，小括号是入参信息，[ 表示数组，L 表示对象，L 后面表示对象
flags:ACC_PUBLIC, ACC_STATIC，表示方法的访问权限修饰符，分别表示public static
方法签名=public static void main(java.lang.String[])
todo stack=2, locals=2, args_size=1，表示执行该方法需要的栈深度，需要在局部变量表中保留槽位数。

无参构造方法的参数为1，不是0，那是因为所有非静态方法都会将this分配到局部变量表的第0号槽位。

4.线程栈与字节码执行模型
每个线程都会有一个线程栈，每次方法调用都会生成一个栈帧，每个栈帧包含操作数栈、局部变量数组、一个class引用组成。
todo 操作数栈和class引用的作用？
局部变量数组=局部变量表，由方法参数和局部变量组成，在编译时确定个数。

5.方法体的字节码解读

6.对象初始化 new init clinit
new dup invokespecial 一定表示创建对象。指令如下：
0: new           #2                  // class demo/jvm01/HelloByteCode
3: dup
4: invokespecial #3                  // Method "<init>":()V
7: astore_1
8: return

new 表示创建对象，但是并没有调用构造函数。
invokespecial表示调用特殊函数，此处指调用构造函数，#3在常量池中表示构造函数，并且无返回值。
dup



haha
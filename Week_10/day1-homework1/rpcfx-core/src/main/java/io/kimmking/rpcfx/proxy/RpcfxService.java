package io.kimmking.rpcfx.proxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE) // 只能放到rpc接口上
@Retention(RetentionPolicy.RUNTIME) // 可在反射时使用
public @interface RpcfxService {
//    String value(); //
}

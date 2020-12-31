package io.kimmking.rpcfx.proxy;

import com.alibaba.fastjson.JSON;
import io.kimmking.rpcfx.api.Filter;
import io.kimmking.rpcfx.api.RpcfxRequest;
import io.kimmking.rpcfx.api.RpcfxResponse;
import io.kimmking.rpcfx.client.Rpcfx;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.reflections.Reflections;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 ApplicationFailedEvent：该事件为spring boot启动失败时的操作

 ApplicationPreparedEvent：上下文context准备时触发

 ApplicationReadyEvent：上下文已经准备完毕的时候触发

 ApplicationStartedEvent：spring boot 启动监听类

 SpringApplicationEvent：获取SpringApplication

 ApplicationEnvironmentPreparedEvent：环境事先准备
 */
@Component
@Order(1)
public class ServiceLoadConfig implements InitializingBean {
    private Map<Class, Object> proxyMaps = new HashMap<>();

    public Map<Class, Object> getProxyMaps() {
        return proxyMaps;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("===========================");
        Reflections reflections = new Reflections("io.kimmking.rpcfx.demo.api");
        Set<Class<?>> serviceClzs = reflections.getTypesAnnotatedWith(RpcfxService.class);
        for (Class<?> serviceClz : serviceClzs) {
            Object proxyInstance = Proxy.newProxyInstance(Rpcfx.class.getClassLoader(), new Class[]{serviceClz}, new RpcfxInvocationHandler(serviceClz, "http://localhost:8081/", null));
            proxyMaps.put(serviceClz, proxyInstance);
        }
        System.out.println("create all serviceClzs proxy instance finish");
    }

    public static class RpcfxInvocationHandler implements InvocationHandler {

        public static final MediaType JSONTYPE = MediaType.get("application/json; charset=utf-8");

        private final Class<?> serviceClass;
        private final String url;
        private final Filter[] filters;

        public <T> RpcfxInvocationHandler(Class<T> serviceClass, String url, Filter... filters) {
            this.serviceClass = serviceClass;
            this.url = url;
            this.filters = filters;
        }


        // 可以尝试，自己去写对象序列化，二进制还是文本的，，，rpcfx是xml自定义序列化、反序列化，json: code.google.com/p/rpcfx
        // int byte char float double long bool
        // [], data class

        @Override
        public Object invoke(Object proxy, Method method, Object[] params) throws Throwable {

            // 加filter地方之二
            // mock == true, new Student("hubao");

            RpcfxRequest request = new RpcfxRequest();
            request.setServiceClass(this.serviceClass.getName());
            request.setMethod(method.getName());
            request.setParams(params);

            if (null!=filters) {
                for (Filter filter : filters) {
                    if (!filter.filter(request)) {
                        return null;
                    }
                }
            }

            RpcfxResponse response = post(request, url);

            // 加filter地方之三
            // Student.setTeacher("cuijing");

            // 这里判断response.status，处理异常
            // 考虑封装一个全局的RpcfxException

            return JSON.parse(response.getResult().toString());
        }

        private RpcfxResponse post(RpcfxRequest req, String url) throws IOException {
            String reqJson = JSON.toJSONString(req);
            System.out.println("req json: "+reqJson);

            // 1.可以复用client
            // 2.尝试使用httpclient或者netty client
            OkHttpClient client = new OkHttpClient();
            final Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(JSONTYPE, reqJson))
                    .build();
            String respJson = client.newCall(request).execute().body().string();
            System.out.println("resp json: "+respJson);
            return JSON.parseObject(respJson, RpcfxResponse.class);
        }
    }
}

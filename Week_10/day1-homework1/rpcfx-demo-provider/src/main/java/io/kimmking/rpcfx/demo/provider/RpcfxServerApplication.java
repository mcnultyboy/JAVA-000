package io.kimmking.rpcfx.demo.provider;

import com.alibaba.fastjson.JSON;
import io.kimmking.rpcfx.api.RpcfxRequest;
import io.kimmking.rpcfx.api.RpcfxResolver;
import io.kimmking.rpcfx.api.RpcfxResponse;
import io.kimmking.rpcfx.api.ServiceProviderDesc;
import io.kimmking.rpcfx.demo.api.OrderService;
import io.kimmking.rpcfx.demo.api.UserService;
import io.kimmking.rpcfx.server.RpcfxInvoker;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.UnknownHostException;

@SpringBootApplication
@RestController
public class RpcfxServerApplication {

	public static void main(String[] args) throws Exception {

		// start zk client
		// CuratorFramework 是Netflix公司开发一款连接zookeeper服务的框架，提供了比较全面的功能，除了基础的节点的操作，节点的监听，还有集群的连接以及重试。
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		CuratorFramework client = CuratorFrameworkFactory.builder().connectString("localhost:2181").namespace("rpcfx").retryPolicy(retryPolicy).build();
		client.start();


		// register service
		// xxx "io.kimmking.rpcfx.demo.api.UserService"

		String userService = "io.kimmking.rpcfx.demo.api.UserService";
		registerService(client, userService);
		String orderService = "io.kimmking.rpcfx.demo.api.OrderService";
		registerService(client, orderService);


		// 进一步的优化，是在spring加载完成后，从里面拿到特定注解的bean，自动注册到zk

		SpringApplication.run(RpcfxServerApplication.class, args);
	}

	private static void registerService(CuratorFramework client, String service) throws Exception {
		String group = "1";
		String version = "1.0.0";
		ServiceProviderDesc userServiceSesc = ServiceProviderDesc.builder()
				.host(InetAddress.getLocalHost().getHostAddress())
				.port(8081)
				.serviceClass(service)
				.group(group)
				.version(version)
				.build();
		// String userServiceSescJson = JSON.toJSONString(userServiceSesc);
		// 判断，节点不存在则创建持久化的根节点
		/**
		 * forPath函数指定创建节点的path和保存的数据，path的指定遵循linux文件path格式，
		 * 创建node时指定的path，父path节点需要存在，否则创建节点失败，比如创建"/parent/child"节点，
		 * 若不存在节点"parent"，那么创建节点会失败。
		 * 在znode中保存的数据需要进行序列化，用户可以选择使用JSON，XML，java内置的序列化机制，
		 * 或者Hession以及Google的protocal Buffer等，为方便讲解，节点存储字符串数据。
		 */

		String parent = "/" + service + "_" + group + "_" + version;
		try {
			if ( null == client.checkExists().forPath(parent)) {
				client.create().withMode(CreateMode.PERSISTENT).forPath(parent, "service".getBytes()); // “service”表示节点存放的数据
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		/**
		 * CreateMode类用于指定创建节点的类型，用户可以选择以下几个参数：

		 CreateMode.PERSISTENT: 创建节点后，不删除就永久存在
		 CreateMode.PERSISTENT_SEQUENTIAL：节点path末尾会追加一个10位数的单调递增的序列
		 CreateMode.EPHEMERAL：创建后，会话话结束节点会自动删除
		 CreateMode.EPHEMERAL_SEQUENTIAL：节点path末尾会追加一个10位数的单调递增的序列
		 */

		// 创建临时的子节点，记录服务的ip port group version等信息
		client.create().withMode(CreateMode.EPHEMERAL).
				forPath( parent + "/" + userServiceSesc.getHost() + "_" + userServiceSesc.getPort(), "provider".getBytes());
	}

	@Autowired
	RpcfxInvoker invoker;

	@PostMapping("/")
	public RpcfxResponse invoke(@RequestBody RpcfxRequest request) {
		return invoker.invoke(request);
	}

	@Bean
	public RpcfxInvoker createInvoker(@Autowired RpcfxResolver resolver){
		return new RpcfxInvoker(resolver);
	}

	@Bean
	public RpcfxResolver demoResolver(){
		return new DemoResolver();
	}

	// 能否去掉name
	//

	// annotation


	@Bean(name = "io.kimmking.rpcfx.demo.api.UserService")
	public UserService createUserService(){
		return new UserServiceImpl();
	}

	@Bean(name = "io.kimmking.rpcfx.demo.api.OrderService")
	public OrderService createOrderService(){
		return new OrderServiceImpl();
	}

}

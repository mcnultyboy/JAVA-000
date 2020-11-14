package com.yb;
import com.yb.entity.OrderByAnno;
import com.yb.entity.OrderByXml;
import com.yb.entity.ProductByXml;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ImportResource;

/***
 * SpringBean的装配方式
 * 1.使用注解，如Component Service Controller Repository等，eg
 *  @Component
 *  public class Order{}
 *
 *
 */
@SpringBootApplication
@ImportResource("classpath:spring.xml")
public class SpringBeanDemoApplication implements ApplicationContextAware, ApplicationRunner{

	private ApplicationContext applicationContext;

	public SpringBeanDemoApplication() {
		System.out.println("SpringBeanDemoApplication instance is created");
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringBeanDemoApplication.class, args);

	}


	/**当前类bean初始化之后调用，不是所有bean初始化之后，此时通过applicationContext并不一定能获取到所有的bean信息*/
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		System.out.println("invoke setApplicationContext");
		this.applicationContext = applicationContext;
	}

	/**所有bean初始化之后调用*/
	@Override
	public void run(ApplicationArguments args) throws Exception {
		System.out.println("applicationContext == " + applicationContext);
		OrderByAnno orderByAnno = applicationContext.getBean(OrderByAnno.class);
		OrderByXml orderByXml = applicationContext.getBean(OrderByXml.class);
		ProductByXml productByXml = applicationContext.getBean(ProductByXml.class);
		System.out.println(orderByAnno); // not null
		System.out.println(orderByXml); // not null
		System.out.println(productByXml); // not null
	}
}

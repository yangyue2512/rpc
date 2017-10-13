package com.ue.rpc.app.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ue.client.server.service.HelloService;
import com.ue.client.server.service.Person;
import com.ue.rpc.client.RpcProxy;

/**
 * 客户端测试接口功能 调用远程服务接口
 * @ClassName: HelloServiceTest 
 * @author yangyue
 * @date 2017年10月13日 下午4:48:37 
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring.xml")
public class HelloServiceTest {

	@Autowired
	private RpcProxy rpcProxy;
	
	@Test
	public void helloTest1() {
		//调用代理create方法， 代理HelloService接口
		HelloService helloService = rpcProxy.create(HelloService.class);
		
		//调用代理的方法 执行invoke
		String result = helloService.hello("hello world");
		System.out.println("服务端返回结果 :" + result);
	}
	
	@Test
	public void helloTest2() {
		HelloService helloService = rpcProxy.create(HelloService.class);
		String result = helloService.hello(new Person("san", "zhang"));
		System.out.println("服务端返回结果：");
		System.out.println(result);
	}
		
}

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
 * �ͻ��˲��Խӿڹ��� ����Զ�̷���ӿ�
 * @ClassName: HelloServiceTest 
 * @author yangyue
 * @date 2017��10��13�� ����4:48:37 
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring.xml")
public class HelloServiceTest {

	@Autowired
	private RpcProxy rpcProxy;
	
	@Test
	public void helloTest1() {
		//���ô���create������ ����HelloService�ӿ�
		HelloService helloService = rpcProxy.create(HelloService.class);
		
		//���ô���ķ��� ִ��invoke
		String result = helloService.hello("hello world");
		System.out.println("����˷��ؽ�� :" + result);
	}
	
	@Test
	public void helloTest2() {
		HelloService helloService = rpcProxy.create(HelloService.class);
		String result = helloService.hello(new Person("san", "zhang"));
		System.out.println("����˷��ؽ����");
		System.out.println(result);
	}
		
}

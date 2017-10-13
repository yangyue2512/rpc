package com.ue.rpc.server.client;

import com.ue.client.server.service.Person;
import com.ue.rpc.server.RpcService;

@RpcService(value = HelloService.class)
public class HelloService implements com.ue.client.server.service.HelloService{

	public String hello(String name) {
		System.out.println("已经调用服务端接口实现，业务处理结果为：");
    	System.out.println("Hello! " + name);
        return "Hello! " + name;
	}

	public String hello(Person person) {
		System.out.println("已经调用服务端接口实现，业务处理为：");
    	System.out.println("Hello! " + person.getFirstName() + " " + person.getLastName());
        return "Hello! " + person.getFirstName() + " " + person.getLastName();
	}

}

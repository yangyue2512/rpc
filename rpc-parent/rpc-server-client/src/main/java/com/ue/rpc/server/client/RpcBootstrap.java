package com.ue.rpc.server.client;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 用户系统服务端的启动入口
 * 其意义是启动springcontext，从而构造框架中的RpcServer
 * 将用户系统中所有标注了RpcService注解的业务发布到RpcServer
 * @ClassName: RpcBootstrap 
 * @author yangyue
 * @date 2017年10月12日 下午12:45:43 
 *
 */
public class RpcBootstrap {

	public static void main(String[] args) {
		new ClassPathXmlApplicationContext("spring.xml");
	}
}














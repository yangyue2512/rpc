package com.ue.client.server.service;
/**
 * rpc框架服务端 客户端使用的接口
 * @ClassName: HelloService 
 * @author yangyue
 * @date 2017年10月12日 下午1:49:08 
 *
 */
public interface HelloService {

	//String参数
	public String hello(String name);
	
	//对象参数
	public String hello(Person person);
}

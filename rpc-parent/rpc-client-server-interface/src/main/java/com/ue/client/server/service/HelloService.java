package com.ue.client.server.service;
/**
 * rpc��ܷ���� �ͻ���ʹ�õĽӿ�
 * @ClassName: HelloService 
 * @author yangyue
 * @date 2017��10��12�� ����1:49:08 
 *
 */
public interface HelloService {

	//String����
	public String hello(String name);
	
	//�������
	public String hello(Person person);
}

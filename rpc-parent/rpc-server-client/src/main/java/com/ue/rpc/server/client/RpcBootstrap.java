package com.ue.rpc.server.client;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * �û�ϵͳ����˵��������
 * ������������springcontext���Ӷ��������е�RpcServer
 * ���û�ϵͳ�����б�ע��RpcServiceע���ҵ�񷢲���RpcServer
 * @ClassName: RpcBootstrap 
 * @author yangyue
 * @date 2017��10��12�� ����12:45:43 
 *
 */
public class RpcBootstrap {

	public static void main(String[] args) {
		new ClassPathXmlApplicationContext("spring.xml");
	}
}














package com.ue.rpc.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

import com.ue.rpc.common.RpcRequest;
import com.ue.rpc.common.RpcResponse;
import com.ue.rpc.registry.ServiceDiscovery;

/**
 * ����RPC�������
 * @ClassName: RpcProxy 
 * @author yangyue
 * @date 2017��10��13�� ����4:52:45 
 *
 */
public class RpcProxy {

	private String serverAddress;
	
	private ServiceDiscovery serviceDiscovery;
	
	public RpcProxy(String serverAddress) {
		this.serverAddress = serverAddress;
	}
	
	public RpcProxy(ServiceDiscovery serviceDiscovery) {
		this.serviceDiscovery = serviceDiscovery;
	}
	
	/**
	 * ��������
	 * @param interfaceClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public<T> T create(Class<?> interfaceClass) {
		
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] {interfaceClass}, new InvocationHandler() {
			
			public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {
				//�����������RpcRequest, ��װ�����������
				RpcRequest rpcRequest = new RpcRequest();
				rpcRequest.setRequestId(UUID.randomUUID().toString());
				
				//�õ��������������ҵ��ӿ�����
				rpcRequest.setClassName(method.getDeclaringClass().getName());;
				rpcRequest.setMethodName(method.getName());
				rpcRequest.setParameterTypes(method.getParameterTypes());
				rpcRequest.setParameters(args);
				
				//���ҷ���
				if (serviceDiscovery != null) {
					serverAddress = serviceDiscovery.discover();
				}
				
				//�����ȡ����ĵ�ַ
				String[] array = serverAddress.split(":");
				String host = array[0];
				int port = Integer.parseInt(array[1]);
				//����Nettyʵ�ֵ�RpcClient ���ӷ�����
				
				RpcClient client = new RpcClient(host, port);
				//ͨ��netty�����˷�������
				RpcResponse response = client.send(rpcRequest);
				
				//��������
				if(response.isError()) {
					throw response.getError();
				} else {
					return response.getResult();
				}
			}
		});
	}

	
}

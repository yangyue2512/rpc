package com.ue.rpc.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

import org.apache.zookeeper.server.Request;

import com.ue.rpc.common.RpcRequest;
import com.ue.rpc.registry.ServiceDiscovery;

/**
 * 创建RPC服务代理
 * @ClassName: RpcProxy 
 * @author yangyue
 * @date 2017年10月13日 下午4:52:45 
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
	 * 创建代理
	 * @param interfaceClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public<T> T create(Class<?> interfaceClass) {
		
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] {interfaceClass}, new InvocationHandler() {
			
			public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {
				//创建请求对象RpcRequest, 封装被代理的属性
				RpcRequest rpcRequest = new RpcRequest();
				rpcRequest.setRequestId(UUID.randomUUID().toString());
				
				//拿到声明这个方法的业务接口名称
				rpcRequest.setClassName(method.getDeclaringClass().getName());;
				rpcRequest.setMethodName(method.getName());
				rpcRequest.setParameterTypes(method.getParameterTypes());
				rpcRequest.setParameters(args);
				
				//查找服务
				if (serviceDiscovery != null) {
					serverAddress = serviceDiscovery.discover();
				}
				
				//随机获取服务的地址
				String[] array = serverAddress.split(":");
				String host = array[0];
				int port = Integer.parseInt(array[1]);
				
				
				
				return null;
			}
		});
	}

	
}

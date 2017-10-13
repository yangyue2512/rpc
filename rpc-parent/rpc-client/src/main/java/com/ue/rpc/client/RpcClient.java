package com.ue.rpc.client;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ue.rpc.common.RpcRequest;
import com.ue.rpc.common.RpcResponse;

/**
 * ��ܵ�RPC�ͻ���  ����rpc����
 * @ClassName: RpcClient 
 * @author yangyue
 * @date 2017��10��13�� ����5:58:55 
 *
 */
public class RpcClient {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(RpcClient.class);

	private String host;
	private int port;

	private RpcResponse response;

	private final Object obj = new Object();

	public RpcClient(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	
	public RpcResponse send(RpcRequest request) {
		EventLoopGroup group = new NioEventLoopGroup();
		
		
		
		return null;
	}
	
	
}

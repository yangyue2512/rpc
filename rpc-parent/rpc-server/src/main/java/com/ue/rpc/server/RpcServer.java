package com.ue.rpc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.ue.rpc.common.RpcDecoder;
import com.ue.rpc.common.RpcEncoder;
import com.ue.rpc.common.RpcRequest;
import com.ue.rpc.common.RpcResponse;
import com.ue.rpc.registry.server.ServiceRegistryServer;

/**
 * rpc�����
 * ��ܵ�RPC�����������ڽ��û�ϵͳ��ҵ�񷢲�ΪRPC����
 * ʹ��ʱ�����û�ͨ��spring-bean�ķ�ʽע�뵽�û���ҵ��ϵͳ��
 * ����ʵ����ApplicationContextAware �� InitializngBean
 * spring���챾��������setApplicationContext()�������Ӷ�����ͨ���Զ���ע�����û���ҵ��ӿں�ʵ��
 * �������afterPropertiesSet()�������ڷ���������netty������
 * @ClassName: RpcServer 
 * @author yangyue ����һ��example
 * @date 2017��10��12�� ����1:59:30 
 *
 */
public class RpcServer implements ApplicationContextAware, InitializingBean{
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(RpcServer.class);

	private String serverAddress;
	private ServiceRegistryServer serviceRegistryServer;
	
	//���ڴ洢ҵ��ӿں�ʵ�����ʵ������(Spring������)
	private Map<String, Object> handlerMap = new HashMap<String, Object>();
	
	public RpcServer(String serverAddress){
		this.serverAddress = serverAddress;
	}
	
	public RpcServer(String serverAddress, ServiceRegistryServer serviceRegistryServer){
		this.serverAddress = serverAddress;
		this.serviceRegistryServer = serviceRegistryServer;
	}
	
	/**
	 * ͨ��ע�⣬��ȡ��ע��rpc����ע���ҵ�����-----�ӿں�impl���󣬽����ŵ�handlerMap�� 
	 */
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {

		Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(RpcService.class);
		
		if(MapUtils.isNotEmpty(serviceBeanMap)){
			for(Object serviceBean : serviceBeanMap.values()){
				//��ҵ��ʵ�����ϵ��Զ���ע���л�õ�value���Ӷ���ȡ��ҵ��ӿ�ȫ��
				String intefaceName = serviceBean.getClass().getAnnotation(RpcService.class).value().getName();
				handlerMap.put(intefaceName, serviceBean);
			}
		}
		
		
		
	}

	/**
	 * �ڴ�����netty���񣬰�handler��ˮ��
	 * 1.�����������ݽ��з����л��õ�request����
	 * 2.����request�еĲ�������RpcHandler��handlerMap���ҵ���Ӧ��ҵ��ʵ�֣�����ָ����������ȡ���ؽ��
	 * 3.��ҵ����ý����װ��response�����л������ͻ���
	 */
	public void afterPropertiesSet() throws Exception {

		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap
					.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel channel) throws Exception {
							
							channel.pipeline()
								.addLast(new RpcDecoder(RpcRequest.class)) // ע����� In -1
								.addLast(new RpcEncoder(RpcResponse.class)) // ���� out 1
								.addLast(new RpcHandler(handlerMap)); //ע��RpcHandler In 2
							
						}
						
					}).option(ChannelOption.SO_BACKLOG, 128)
					.childOption(ChannelOption.SO_KEEPALIVE, true);
			
				String[] strings = serverAddress.split(":");
				String host = strings[0];
				int port = Integer.parseInt(strings[1]);
				
				ChannelFuture future = bootstrap.bind(host, port);
				LOGGER.debug("server started on port {}", port);
				
				if(serverAddress != null) {
					serviceRegistryServer.register(serverAddress);
				}
				future.channel().closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
			
	} 

	
	
}

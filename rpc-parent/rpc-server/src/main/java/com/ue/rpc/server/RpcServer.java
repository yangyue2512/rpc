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
 * rpc服务端
 * 框架的RPC服务器（用于将用户系统的业务发布为RPC服务）
 * 使用时可由用户通过spring-bean的方式注入到用户的业务系统中
 * 本类实现了ApplicationContextAware 和 InitializngBean
 * spring构造本对象会调用setApplicationContext()方法，从而可以通过自定义注解获得用户的业务接口和实现
 * 还会调用afterPropertiesSet()方法，在方法中启动netty服务器
 * @ClassName: RpcServer 
 * @author yangyue 这是一个example
 * @date 2017年10月12日 下午1:59:30 
 *
 */
public class RpcServer implements ApplicationContextAware, InitializingBean{
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(RpcServer.class);

	private String serverAddress;
	private ServiceRegistryServer serviceRegistryServer;
	
	//用于存储业务接口和实现类的实例对象(Spring所构造)
	private Map<String, Object> handlerMap = new HashMap<String, Object>();
	
	public RpcServer(String serverAddress){
		this.serverAddress = serverAddress;
	}
	
	public RpcServer(String serverAddress, ServiceRegistryServer serviceRegistryServer){
		this.serverAddress = serverAddress;
		this.serviceRegistryServer = serviceRegistryServer;
	}
	
	/**
	 * 通过注解，获取标注了rpc服务注解的业务类的-----接口和impl对象，将它放到handlerMap中 
	 */
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {

		Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(RpcService.class);
		
		if(MapUtils.isNotEmpty(serviceBeanMap)){
			for(Object serviceBean : serviceBeanMap.values()){
				//从业务实现类上的自定义注解中获得到value，从而获取到业务接口全名
				String intefaceName = serviceBean.getClass().getAnnotation(RpcService.class).value().getName();
				handlerMap.put(intefaceName, serviceBean);
			}
		}
		
		
		
	}

	/**
	 * 在此启动netty服务，绑定handler流水线
	 * 1.接收请求数据进行反序列化得到request对象
	 * 2.根据request中的参数，让RpcHandler从handlerMap中找到对应的业务实现，调用指定方法，获取返回结果
	 * 3.待业务调用结果封装到response并序列化后发往客户端
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
								.addLast(new RpcDecoder(RpcRequest.class)) // 注册解码 In -1
								.addLast(new RpcEncoder(RpcResponse.class)) // 编码 out 1
								.addLast(new RpcHandler(handlerMap)); //注册RpcHandler In 2
							
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

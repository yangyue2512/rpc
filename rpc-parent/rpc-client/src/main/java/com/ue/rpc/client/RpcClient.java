package com.ue.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ue.rpc.common.RpcDecoder;
import com.ue.rpc.common.RpcEncoder;
import com.ue.rpc.common.RpcRequest;
import com.ue.rpc.common.RpcResponse;


/**
 * 框架的RPC客户端  发送rpc请求
 * @ClassName: RpcClient 
 * @author yangyue
 * @date 2017年10月13日 下午5:58:55 
 *
 */
public class RpcClient extends SimpleChannelInboundHandler<RpcResponse>{
	
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
	
	/**
	 * 读数据 并发送数据给服务器
	 */
	public RpcResponse send(RpcRequest request) throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		
		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel channel) throws Exception {
					
					//向pipeline中添加编码、解码、业务处理的handler
					channel.pipeline()
							.addLast(new RpcEncoder(RpcRequest.class))  // 先写出去 out 1
							.addLast(new RpcDecoder(RpcResponse.class)) //在编码 成对象 //In 1
							.addLast(RpcClient.this);
					
				}
			}).option(ChannelOption.SO_KEEPALIVE, true);
			//链接服务器
			ChannelFuture future = bootstrap.connect(host, port).sync();
			//将request对象写入outbundle处理后发出（即RpcEncoder编码器）
			future.channel().writeAndFlush(request).sync();

			// 用线程等待的方式决定是否关闭连接
			// 其意义是：先在此阻塞，等待获取到服务端的返回后，被唤醒，从而关闭网络连接
			synchronized (obj) {
				obj.wait();
			}
			if (response != null) {
				future.channel().closeFuture().sync();
			}
			return response;
			
		} finally {
			group.shutdownGracefully();
		}
	}

	//读取服务端的返回结果
	@Override
	protected void channelRead0(ChannelHandlerContext context, RpcResponse response)
			throws Exception {
		this.response = response;
		
		synchronized (obj) {
			obj.notifyAll();
		}
	}
	
	/**
	 * 异常处理
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		LOGGER.error("client caught exception", cause);
		ctx.close();
	}
	
	
}

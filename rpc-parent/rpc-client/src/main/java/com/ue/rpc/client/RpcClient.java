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
 * ��ܵ�RPC�ͻ���  ����rpc����
 * @ClassName: RpcClient 
 * @author yangyue
 * @date 2017��10��13�� ����5:58:55 
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
	 * ������ ���������ݸ�������
	 */
	public RpcResponse send(RpcRequest request) throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		
		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel channel) throws Exception {
					
					//��pipeline����ӱ��롢���롢ҵ�����handler
					channel.pipeline()
							.addLast(new RpcEncoder(RpcRequest.class))  // ��д��ȥ out 1
							.addLast(new RpcDecoder(RpcResponse.class)) //�ڱ��� �ɶ��� //In 1
							.addLast(RpcClient.this);
					
				}
			}).option(ChannelOption.SO_KEEPALIVE, true);
			//���ӷ�����
			ChannelFuture future = bootstrap.connect(host, port).sync();
			//��request����д��outbundle����󷢳�����RpcEncoder��������
			future.channel().writeAndFlush(request).sync();

			// ���̵߳ȴ��ķ�ʽ�����Ƿ�ر�����
			// �������ǣ����ڴ��������ȴ���ȡ������˵ķ��غ󣬱����ѣ��Ӷ��ر���������
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

	//��ȡ����˵ķ��ؽ��
	@Override
	protected void channelRead0(ChannelHandlerContext context, RpcResponse response)
			throws Exception {
		this.response = response;
		
		synchronized (obj) {
			obj.notifyAll();
		}
	}
	
	/**
	 * �쳣����
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		LOGGER.error("client caught exception", cause);
		ctx.close();
	}
	
	
}

package com.ue.rpc.server;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ue.rpc.common.RpcRequest;
import com.ue.rpc.common.RpcResponse;

/**
 * ��������ҵ�����
 * ͨ������ʱ����� ҵ��ӿڼ�ʵ�� handlerMap �����ÿͻ����������ҵ�񷽷�
 * ����ҵ�񷵻�ֵ��װ��response��������һ��handler 
 * @ClassName: RpcHandler 
 * @author yangyue
 * @date 2017��10��12�� ����3:15:01 
 *
 */
public class RpcHandler extends SimpleChannelInboundHandler<RpcRequest>{

	private static final Logger LOGGER = LoggerFactory
			.getLogger(RpcHandler.class);

	private final Map<String, Object> handlerMap;
	
	public RpcHandler(Map<String, Object> handlerMap) {
		this.handlerMap = handlerMap;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request)
			throws Exception {

		RpcResponse response = new RpcResponse();
		response.setRequestId(request.getRequestId());
		
		//����request����������ҵ�����
		try {
			Object result = handle(request);
			response.setResult(result);
		} catch (Throwable e) {
			response.setError(e);
		}
		//д��outbundle(��RpcEncoder) ������һ������ ���� ���͵�channel���͸��ͻ���
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}
	
	/**
	 * ����request����������ҵ�����
	 * ������ͨ������ķ�ʽ��ɵ�
	 * @param request
	 * @return
	 * @throws Throwable
	 */
	private Object handle(RpcRequest request) throws Throwable{
		String className = request.getClassName();
		
		//�õ�ʵ�������
		Object serviceBean = handlerMap.get(className);
		
		//�õ�Ҫ���õķ��������������� ����ֵ
		String methodName = request.getMethodName();
		Class<?>[] parameterTypes = request.getParameterTypes();
		Object[] parameters = request.getParameters();
		
		//�õ��ӿ���
		Class<?> forName = Class.forName(className);
		
		//����ʵ��������ָ�����������ؽ��
		Method method = forName.getMethod(methodName, parameterTypes);
		return method.invoke(serviceBean, parameters);
		
	}
	
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		LOGGER.error("server caught exception", cause);
		ctx.close();
	}

}

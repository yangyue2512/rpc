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
 * 处理具体的业务调用
 * 通过构造时出入的 业务接口及实现 handlerMap 来调用客户端所请求的业务方法
 * 并将业务返回值封装成response对象传入下一个handler 
 * @ClassName: RpcHandler 
 * @author yangyue
 * @date 2017年10月12日 下午3:15:01 
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
		
		//根据request来处理具体的业务调用
		try {
			Object result = handle(request);
			response.setResult(result);
		} catch (Throwable e) {
			response.setError(e);
		}
		//写入outbundle(即RpcEncoder) 进行下一步处理 编码 后发送到channel发送给客户端
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}
	
	/**
	 * 根据request来处理具体的业务调用
	 * 调用是通过反射的方式完成的
	 * @param request
	 * @return
	 * @throws Throwable
	 */
	private Object handle(RpcRequest request) throws Throwable{
		String className = request.getClassName();
		
		//拿到实现类对象
		Object serviceBean = handlerMap.get(className);
		
		//拿到要调用的方法名、参数类型 参数值
		String methodName = request.getMethodName();
		Class<?>[] parameterTypes = request.getParameterTypes();
		Object[] parameters = request.getParameters();
		
		//拿到接口类
		Class<?> forName = Class.forName(className);
		
		//调用实现类对象的指定方法并返回结果
		Method method = forName.getMethod(methodName, parameterTypes);
		return method.invoke(serviceBean, parameters);
		
	}
	
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		LOGGER.error("server caught exception", cause);
		ctx.close();
	}

}

package com.ue.common;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * RPC解码器
 * @Title: RpcDecoder.java 
 * @Package com.ue.common 
 * @author yangyue   
 * @date 2017年10月11日 下午11:45:30 
 * @version V1.0
 */
public class RpcDecoder extends ByteToMessageDecoder{

	private Class<?> generiClass;
	
	//构造函数传入反序列化的class
	public RpcDecoder(Class<?> generClass) {
		this.generiClass = generClass;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		//netty中 输入参数小于4无效
		if (in.readableBytes() < 4) {
			return;
		}
		in.markReaderIndex();
		int dataLength = in.readInt();
		if(dataLength < 0){
			ctx.close();
		}
		if(in.readableBytes() < dataLength){
			in.resetReaderIndex();
		}
		
		//将ByteBuf转化为byte[]
		byte[] data = new byte[dataLength];
		in.readBytes(data);
		//data转换成object
		Object obj = SerializationUtil.deserialize(data, generiClass);
		out.add(obj);
	}
	
	
	
}

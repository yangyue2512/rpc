package com.ue.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * RPC 编码器
 * @Title: RpcEncoder.java 
 * @Package com.ue.common 
 * @author yangyue   
 * @date 2017年10月11日 下午11:59:41 
 * @version V1.0
 */
public class RpcEncoder extends MessageToByteEncoder<Object> {

	private Class<?> genericClass;

	// 构造函数传入向反序列化的class
	public RpcEncoder(Class<?> genericClass) {
		this.genericClass = genericClass;
	}

	@Override
	public void encode(ChannelHandlerContext ctx, Object inob, ByteBuf out)
			throws Exception {
		//序列化
		if (genericClass.isInstance(inob)) {
			byte[] data = SerializationUtil.serialize(inob);
			out.writeInt(data.length);
			out.writeBytes(data);
		}
	}
}
package com.ue.rpc.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * RPC ������
 * @Title: RpcEncoder.java 
 * @Package com.ue.common 
 * @author yangyue   
 * @date 2017��10��11�� ����11:59:41 
 * @version V1.0
 */
public class RpcEncoder extends MessageToByteEncoder<Object> {

	private Class<?> genericClass;

	// ���캯���������л���class
	public RpcEncoder(Class<?> genericClass) {
		this.genericClass = genericClass;
	}

	@Override
	public void encode(ChannelHandlerContext ctx, Object inob, ByteBuf out)
			throws Exception {
		//���л�
		if (genericClass.isInstance(inob)) {
			byte[] data = SerializationUtil.serialize(inob);
			out.writeInt(data.length);
			out.writeBytes(data);
		}
	}
}
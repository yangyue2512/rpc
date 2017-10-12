package com.ue.rpc.common;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * RPC������
 * @Title: RpcDecoder.java 
 * @Package com.ue.common 
 * @author yangyue   
 * @date 2017��10��11�� ����11:45:30 
 * @version V1.0
 */
public class RpcDecoder extends ByteToMessageDecoder{

	private Class<?> generiClass;
	
	//���캯���뷴���л���class
	public RpcDecoder(Class<?> generClass) {
		this.generiClass = generClass;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		//netty�� �������С��4��Ч
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
		
		//��ByteBufת��Ϊbyte[]
		byte[] data = new byte[dataLength];
		in.readBytes(data);
		//dataת����object
		Object obj = SerializationUtil.deserialize(data, generiClass);
		out.add(obj);
	}
	
	
	
}

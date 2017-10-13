package com.ue.rpc.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

/**
 * 解码
 * @ClassName: RpcDecoder 
 * @author yangyue
 * @date 2017年10月17日 下午2:20:47 
 *
 */
public class RpcDecoder extends ByteToMessageDecoder {

    private Class<?> genericClass;

	// 鏋勯�鍑芥暟浼犲叆鍚戝弽搴忓垪鍖栫殑class
    public RpcDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    public final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4) {
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        if (dataLength < 0) {
            ctx.close();
        }
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
        }
        //灏咮yteBuf杞崲涓篵yte[]
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        //灏哾ata杞崲鎴恛bject
        Object obj = SerializationUtil.deserialize(data, genericClass);
        out.add(obj);
    }
}

package com.ue.rpc.server;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;

/**
 * RPC �Զ���ע��  (����ڷ���ʵ������)
 * @ClassName: RpcService 
 * @author yangyue
 * @date 2017��10��12�� ����2:33:01 
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)//VM����������Ҳ����ע�ͣ���˿���ͨ��������ƶ�ȡע�����Ϣ
@Component
public @interface RpcService {
	
	Class<?> value();
}

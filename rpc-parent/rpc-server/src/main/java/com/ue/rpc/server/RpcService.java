package com.ue.rpc.server;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;

/**
 * RPC 自定义注解  (标记在服务实现类上)
 * @ClassName: RpcService 
 * @author yangyue
 * @date 2017年10月12日 下午2:33:01 
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)//VM将在运行期也保留注释，因此可以通过反射机制读取注解的信息
@Component
public @interface RpcService {
	
	Class<?> value();
}

package com.ue.rpc.common;

/**
 * RPC请求数据模型
 * @ClassName: RpcRequest 
 * @author yangyue
 * @date 2017年10月13日 上午10:28:20 
 *
 */
public class RpcRequest {
	
	    private String requestId;
	    private String className;
	    private String methodName;
	    private Class<?>[] parameterTypes;
	    private Object[] parameters;

	    public String getRequestId() {
	        return requestId;
	    }

	    public void setRequestId(String requestId) {
	        this.requestId = requestId;
	    }

	    public String getClassName() {
	        return className;
	    }

	    public void setClassName(String className) {
	        this.className = className;
	    }

	    public String getMethodName() {
	        return methodName;
	    }

	    public void setMethodName(String methodName) {
	        this.methodName = methodName;
	    }

	    public Class<?>[] getParameterTypes() {
	        return parameterTypes;
	    }

	    public void setParameterTypes(Class<?>[] parameterTypes) {
	        this.parameterTypes = parameterTypes;
	    }

	    public Object[] getParameters() {
	        return parameters;
	    }

	    public void setParameters(Object[] parameters) {
	        this.parameters = parameters;
	    }
}

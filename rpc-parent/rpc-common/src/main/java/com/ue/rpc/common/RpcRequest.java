package com.ue.rpc.common;

/**
 * 请求参数的封装
 * @ClassName: RpcRequest 
 * @author yangyue
 * @date 2017年10月17日 下午2:21:34 
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

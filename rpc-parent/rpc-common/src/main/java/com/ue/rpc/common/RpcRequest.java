package com.ue.rpc.common;

/**
 * ��װ RPC ����
 * ��װ���͵�object�ķ�������
 * @Title: RpcRequest.java 
 * @Package com.ue.common 
 * @author yangyue   
 * @date 2017��10��12�� ����12:01:52 
 * @version V1.0
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

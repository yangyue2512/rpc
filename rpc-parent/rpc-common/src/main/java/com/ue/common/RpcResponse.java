package com.ue.common;

/**
 * 封装 RPC 响应
 * 封装相应object
 * @Title: RpcResponse.java 
 * @Package com.ue.common 
 * @author yangyue   
 * @date 2017年10月12日 上午12:07:40 
 * @version V1.0
 */
public class RpcResponse {

    private String requestId;
    private Throwable error;
    private Object result;

    public boolean isError() {
        return error != null;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}

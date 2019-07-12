package com.test;

import java.io.Serializable;

/**
 * serversub
 */
public  class  ClassInfo implements Serializable {
    /**
     * 序列化id
     */
    /**
     * 客户端存根的作用是把请求的参数以约定的通讯协议打包好发送给服务端然后解析服务端返回消息
     */
    private String className;//类名

    private String methodName;//返回值

    private Class<?>[] types; //参数类型

    private Object[] objects; //参数列表


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

    public Class<?>[] getTypes() {
        return types;
    }

    public void setTypes(Class<?>[] types) {
        this.types = types;
    }

    public Object[] getObjects() {
        return objects;
    }

    public void setObjects(Object[] objects) {
        this.objects = objects;
    }
}

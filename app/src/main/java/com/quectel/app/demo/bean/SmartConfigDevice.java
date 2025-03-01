package com.quectel.app.demo.bean;

import com.quectel.sdk.smart.config.api.bean.QuecPairDeviceBean;

public class SmartConfigDevice {

    private QuecPairDeviceBean deviceBean;
    private int bindResult;
    private String message;

    public QuecPairDeviceBean getDeviceBean() {
        return deviceBean;
    }

    public void setDeviceBean(QuecPairDeviceBean deviceBean) {
        this.deviceBean = deviceBean;
    }

    public int getBindResult() {
        return bindResult;
    }

    public void setBindResult(int bindResult) {
        this.bindResult = bindResult;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

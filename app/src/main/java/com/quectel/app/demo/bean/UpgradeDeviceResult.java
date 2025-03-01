package com.quectel.app.demo.bean;

import com.quectel.basic.common.entity.QuecBaseModel;

import java.util.List;

public class UpgradeDeviceResult implements QuecBaseModel {

    private List<DeviceOtaResult> successList;

    private List<DeviceOtaResult> failList;

    public List<DeviceOtaResult> getSuccessList() {
        return successList;
    }

    public void setSuccessList(List<DeviceOtaResult> successList) {
        this.successList = successList;
    }

    public List<DeviceOtaResult> getFailList() {
        return failList;
    }

    public void setFailList(List<DeviceOtaResult> failList) {
        this.failList = failList;
    }
}

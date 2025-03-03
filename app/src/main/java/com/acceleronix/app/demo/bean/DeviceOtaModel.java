package com.acceleronix.app.demo.bean;

public class DeviceOtaModel {
    private String productKey;
    private String deviceKey;
    private long planId;
    private String deviceName;
    private String version;
    private String desc;
    /**
     * 设备升级状态
     * {@link com.quectel.sdk.ota.upgrade.model.OtaUpgradeStatus}
     */
    private int deviceStatus;
    private int userConfirmStatus;
    private String productIcon;
    private float upgradeProgress;


    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }

    public String getDeviceKey() {
        return deviceKey;
    }

    public void setDeviceKey(String deviceKey) {
        this.deviceKey = deviceKey;
    }

    public long getPlanId() {
        return planId;
    }

    public void setPlanId(long planId) {
        this.planId = planId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(int deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public int getUserConfirmStatus() {
        return userConfirmStatus;
    }

    public void setUserConfirmStatus(int userConfirmStatus) {
        this.userConfirmStatus = userConfirmStatus;
    }

    public String getProductIcon() {
        return productIcon;
    }

    public void setProductIcon(String productIcon) {
        this.productIcon = productIcon;
    }

    public float getUpgradeProgress() {
        return upgradeProgress;
    }

    public void setUpgradeProgress(float upgradeProgress) {
        this.upgradeProgress = upgradeProgress;
    }
}

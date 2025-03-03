package com.acceleronix.app.demo.bean;

import java.io.Serializable;

public class SharedDevice implements Serializable {

    private String dgid;
    private String groupName;
    private String pk;
    private String dk;
    private String deviceName;

    public String getDgid() {
        return dgid;
    }

    public void setDgid(String dgid) {
        this.dgid = dgid;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    public String getDk() {
        return dk;
    }

    public void setDk(String dk) {
        this.dk = dk;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}

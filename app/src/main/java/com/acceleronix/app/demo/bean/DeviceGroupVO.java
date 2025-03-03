package com.acceleronix.app.demo.bean;

import java.io.Serializable;

public class DeviceGroupVO implements Serializable {

    private String dgid;
    private String parentId;
    private String address;
    private String coordinate;
    private String coordinateSystem;
    private String name;
    private String description;
    private String contactPhoneList;
    private String manager;
    private String managerType;
    private String owner;
    private String extend;
    private String addTime;
    private int deviceGroupType;
    private String shareCode;

    public String getDgid() {
        return dgid;
    }

    public void setDgid(String dgid) {
        this.dgid = dgid;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }

    public String getCoordinateSystem() {
        return coordinateSystem;
    }

    public void setCoordinateSystem(String coordinateSystem) {
        this.coordinateSystem = coordinateSystem;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactPhoneList() {
        return contactPhoneList;
    }

    public void setContactPhoneList(String contactPhoneList) {
        this.contactPhoneList = contactPhoneList;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getManagerType() {
        return managerType;
    }

    public void setManagerType(String managerType) {
        this.managerType = managerType;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public int getDeviceGroupType() {
        return deviceGroupType;
    }

    public void setDeviceGroupType(int deviceGroupType) {
        this.deviceGroupType = deviceGroupType;
    }

    public String getShareCode() {
        return shareCode;
    }

    public void setShareCode(String shareCode) {
        this.shareCode = shareCode;
    }
}

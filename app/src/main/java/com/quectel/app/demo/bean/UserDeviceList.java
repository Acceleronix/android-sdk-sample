package com.quectel.app.demo.bean;

import android.text.TextUtils;
import android.util.Base64;

import com.quectel.basic.common.entity.QuecDeviceModel;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class UserDeviceList implements Serializable {

    private int code;
    private String msg;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean implements Serializable {
        private int total;
        private List<ListBean> list;
        private int pageNum;
        private int pageSize;
        private int size;
        private int startRow;
        private int endRow;
        private int pages;
        private int prePage;
        private int nextPage;
        private boolean isFirstPage;
        private boolean isLastPage;
        private boolean hasPreviousPage;
        private boolean hasNextPage;
        private int navigatePages;
        private List<Integer> navigatepageNums;
        private int navigateFirstPage;
        private int navigateLastPage;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public int getPageNum() {
            return pageNum;
        }

        public void setPageNum(int pageNum) {
            this.pageNum = pageNum;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getStartRow() {
            return startRow;
        }

        public void setStartRow(int startRow) {
            this.startRow = startRow;
        }

        public int getEndRow() {
            return endRow;
        }

        public void setEndRow(int endRow) {
            this.endRow = endRow;
        }

        public int getPages() {
            return pages;
        }

        public void setPages(int pages) {
            this.pages = pages;
        }

        public int getPrePage() {
            return prePage;
        }

        public void setPrePage(int prePage) {
            this.prePage = prePage;
        }

        public int getNextPage() {
            return nextPage;
        }

        public void setNextPage(int nextPage) {
            this.nextPage = nextPage;
        }

        public boolean isIsFirstPage() {
            return isFirstPage;
        }

        public void setIsFirstPage(boolean isFirstPage) {
            this.isFirstPage = isFirstPage;
        }

        public boolean isIsLastPage() {
            return isLastPage;
        }

        public void setIsLastPage(boolean isLastPage) {
            this.isLastPage = isLastPage;
        }

        public boolean isHasPreviousPage() {
            return hasPreviousPage;
        }

        public void setHasPreviousPage(boolean hasPreviousPage) {
            this.hasPreviousPage = hasPreviousPage;
        }

        public boolean isHasNextPage() {
            return hasNextPage;
        }

        public void setHasNextPage(boolean hasNextPage) {
            this.hasNextPage = hasNextPage;
        }

        public int getNavigatePages() {
            return navigatePages;
        }

        public void setNavigatePages(int navigatePages) {
            this.navigatePages = navigatePages;
        }

        public List<Integer> getNavigatepageNums() {
            return navigatepageNums;
        }

        public void setNavigatepageNums(List<Integer> navigatepageNums) {
            this.navigatepageNums = navigatepageNums;
        }

        public int getNavigateFirstPage() {
            return navigateFirstPage;
        }

        public void setNavigateFirstPage(int navigateFirstPage) {
            this.navigateFirstPage = navigateFirstPage;
        }

        public int getNavigateLastPage() {
            return navigateLastPage;
        }

        public void setNavigateLastPage(int navigateLastPage) {
            this.navigateLastPage = navigateLastPage;
        }

        public static class ListBean implements Serializable {
            private String deviceKey;
            private String productKey;
            private String deviceName;
            private String deviceStatus;
            private String deviceCreateTime;
            private String activeTime;
            private String deviceBindTime;
            private String lastConnTime;
            private String protocol;
            private String productName;
            private String uid;
            private String userName;
            private String phone;
            private String locateType;
            private int deviceType;
            private String ownerUid;
            private String shareCode;
            private String authKey;
            private String bindType;
            private String authCode;
            private String btPwd;
            private int verified;
            private int status;
            private String signalStrength;
            private String lastOfflineTime;

            private int capabilitiesBitmask;

            private int onlineStatus;


            public String getBindingCode() {
                return bindingCode;
            }

            public void setBindingCode(String bindingCode) {
                this.bindingCode = bindingCode;
            }

            private String bindingCode;


            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public String getSignalStrength() {
                return signalStrength;
            }

            public void setSignalStrength(String signalStrength) {
                this.signalStrength = signalStrength;
            }

            public String getLastOfflineTime() {
                return lastOfflineTime;
            }

            public void setLastOfflineTime(String lastOfflineTime) {
                this.lastOfflineTime = lastOfflineTime;
            }

            public String getDeviceKey() {
                return deviceKey;
            }

            public void setDeviceKey(String deviceKey) {
                this.deviceKey = deviceKey;
            }

            public String getProductKey() {
                return productKey;
            }

            public void setProductKey(String productKey) {
                this.productKey = productKey;
            }

            public String getDeviceName() {
                return deviceName;
            }

            public void setDeviceName(String deviceName) {
                this.deviceName = deviceName;
            }

            public String getDeviceStatus() {
                return deviceStatus;
            }

            public void setDeviceStatus(String deviceStatus) {
                this.deviceStatus = deviceStatus;
            }

            public String getDeviceCreateTime() {
                return deviceCreateTime;
            }

            public void setDeviceCreateTime(String deviceCreateTime) {
                this.deviceCreateTime = deviceCreateTime;
            }

            public String getActiveTime() {
                return activeTime;
            }

            public void setActiveTime(String activeTime) {
                this.activeTime = activeTime;
            }

            public String getDeviceBindTime() {
                return deviceBindTime;
            }

            public void setDeviceBindTime(String deviceBindTime) {
                this.deviceBindTime = deviceBindTime;
            }

            public String getLastConnTime() {
                return lastConnTime;
            }

            public void setLastConnTime(String lastConnTime) {
                this.lastConnTime = lastConnTime;
            }

            public String getProtocol() {
                return protocol;
            }

            public void setProtocol(String protocol) {
                this.protocol = protocol;
            }

            public String getProductName() {
                return productName;
            }

            public void setProductName(String productName) {
                this.productName = productName;
            }

            public String getUid() {
                return uid;
            }

            public void setUid(String uid) {
                this.uid = uid;
            }

            public String getUserName() {
                return userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public String getLocateType() {
                return locateType;
            }

            public void setLocateType(String locateType) {
                this.locateType = locateType;
            }

            public int getDeviceType() {
                return deviceType;
            }

            public void setDeviceType(int deviceType) {
                this.deviceType = deviceType;
            }

            public String getOwnerUid() {
                return ownerUid;
            }

            public void setOwnerUid(String ownerUid) {
                this.ownerUid = ownerUid;
            }

            public String getShareCode() {
                return shareCode;
            }

            public void setShareCode(String shareCode) {
                this.shareCode = shareCode;
            }

            public String getAuthKey() {
                return authKey;
            }

            public void setAuthKey(String authKey) {
                this.authKey = authKey;
            }

            public String getBindType() {
                return bindType;
            }

            public void setBindType(String bindType) {
                this.bindType = bindType;
            }

            public String getAuthCode() {
                return authCode;
            }

            public void setAuthCode(String authCode) {
                this.authCode = authCode;
            }

            public String getBtPwd() {
                return btPwd;
            }

            public void setBtPwd(String btPwd) {
                this.btPwd = btPwd;
            }

            public int getVerified() {
                return verified;
            }

            public void setVerified(int verified) {
                this.verified = verified;
            }

            public int getCapabilitiesBitmask() {
                return capabilitiesBitmask;
            }

            public void setCapabilitiesBitmask(int capabilitiesBitmask) {
                this.capabilitiesBitmask = capabilitiesBitmask;
            }

            public int getOnlineStatus() {
                return onlineStatus;
            }

            public void setOnlineStatus(int onlineStatus) {
                this.onlineStatus = onlineStatus;
            }

            public String getChannelId() {
                return productKey + "_" + deviceKey;
            }

            public QuecDeviceModel toQuecDeviceModel() {
                QuecDeviceModel model = new QuecDeviceModel();
                model.setPk(productKey);
                model.setDk(deviceKey);
                model.setBindingkey(bindingCode);
                model.setCapabilitiesBitmask(capabilitiesBitmask);
                model.setOnlineStatus(onlineStatus);
                model.setVerified(verified + "");

                if (capabilitiesBitmask == 4){
                    String bindingKey = null;
                    if (!TextUtils.isEmpty(bindingCode)) {
                        bindingKey = Base64.encodeToString(bindingCode.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
                    }
                    model.setBindingkey(bindingKey);
                } else {
                    model.setBindingkey(authKey);
                }

                return model;
            }
        }
    }
}

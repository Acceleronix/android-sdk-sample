package com.acceleronix.app.demo.bean;

import java.io.Serializable;

public class UserInfor implements Serializable {

    private int code;
    private String msg;
    private DataDTO data;

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

    public DataDTO getData() {
        return data;
    }

    public void setData(DataDTO data) {
        this.data = data;
    }

    public static class DataDTO implements Serializable{
        private String uid;
        private String phone;
        private String nikeName;
        private String sex;
        private String address;
        private String email;
        private String headimg;
        private String wechatMiniprogramUserId;
        private String wechatUnionId;
        private String twitterUserId;
        private String facebookUserId;
        private String alipayUserId;
        private String qqUserId;
        private String wechatOffiaccountUserId;
        private String registerTime;
        private String lastLoginTime;
        private String timezone;
        private String nationality;
        private String lang;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getNikeName() {
            return nikeName;
        }

        public void setNikeName(String nikeName) {
            this.nikeName = nikeName;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getHeadimg() {
            return headimg;
        }

        public void setHeadimg(String headimg) {
            this.headimg = headimg;
        }

        public String getWechatMiniprogramUserId() {
            return wechatMiniprogramUserId;
        }

        public void setWechatMiniprogramUserId(String wechatMiniprogramUserId) {
            this.wechatMiniprogramUserId = wechatMiniprogramUserId;
        }

        public String getWechatUnionId() {
            return wechatUnionId;
        }

        public void setWechatUnionId(String wechatUnionId) {
            this.wechatUnionId = wechatUnionId;
        }

        public String getTwitterUserId() {
            return twitterUserId;
        }

        public void setTwitterUserId(String twitterUserId) {
            this.twitterUserId = twitterUserId;
        }

        public String getFacebookUserId() {
            return facebookUserId;
        }

        public void setFacebookUserId(String facebookUserId) {
            this.facebookUserId = facebookUserId;
        }

        public String getAlipayUserId() {
            return alipayUserId;
        }

        public void setAlipayUserId(String alipayUserId) {
            this.alipayUserId = alipayUserId;
        }

        public String getQqUserId() {
            return qqUserId;
        }

        public void setQqUserId(String qqUserId) {
            this.qqUserId = qqUserId;
        }

        public String getWechatOffiaccountUserId() {
            return wechatOffiaccountUserId;
        }

        public void setWechatOffiaccountUserId(String wechatOffiaccountUserId) {
            this.wechatOffiaccountUserId = wechatOffiaccountUserId;
        }

        public String getRegisterTime() {
            return registerTime;
        }

        public void setRegisterTime(String registerTime) {
            this.registerTime = registerTime;
        }

        public String getLastLoginTime() {
            return lastLoginTime;
        }

        public void setLastLoginTime(String lastLoginTime) {
            this.lastLoginTime = lastLoginTime;
        }

        public String getTimezone() {
            return timezone;
        }

        public void setTimezone(String timezone) {
            this.timezone = timezone;
        }

        public String getNationality() {
            return nationality;
        }

        public void setNationality(String nationality) {
            this.nationality = nationality;
        }

        public String getLang() {
            return lang;
        }

        public void setLang(String lang) {
            this.lang = lang;
        }
    }
}

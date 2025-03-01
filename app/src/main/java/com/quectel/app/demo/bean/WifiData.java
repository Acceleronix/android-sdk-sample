package com.quectel.app.demo.bean;

public class WifiData {

    private String authCode;
    private String pk;
    private String dk;
    private boolean effective = false;

    public boolean isEffective() {
        return effective;
    }

    public void setEffective(boolean effective) {
        this.effective = effective;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
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

    @Override
    public String toString() {
        return "WifiData{" +
                "authCode='" + authCode + '\'' +
                ", pk='" + pk + '\'' +
                ", dk='" + dk + '\'' +
                '}';
    }



}

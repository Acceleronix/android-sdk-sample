package com.quectel.app.demo.bean;

import java.io.Serializable;

public class LanVO implements Serializable {

    private int id;
    private String val;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return "LanVO{" +
                "id=" + id +
                ", val='" + val + '\'' +
                '}';
    }
}

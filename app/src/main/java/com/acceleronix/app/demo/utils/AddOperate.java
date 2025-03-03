package com.acceleronix.app.demo.utils;

import java.math.BigDecimal;

public class AddOperate {

    public static String add(String v1, String v2)
    {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.add(b2).toString();
    }

    public static String sub(String v1, String v2)
    {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.subtract(b2).toString();
    }


}

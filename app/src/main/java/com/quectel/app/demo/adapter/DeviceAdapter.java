package com.quectel.app.demo.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.quectel.app.demo.R;
import com.quectel.app.demo.bean.UserDeviceList;

import java.util.List;

public class DeviceAdapter extends BaseQuickAdapter<UserDeviceList.DataBean.ListBean, BaseViewHolder> {

    private Context mContext;

    public DeviceAdapter(Context context, List data) {
        super(R.layout.device_item, data);
        this.mContext  = context;
    }
    @Override
    protected void convert(BaseViewHolder helper, final UserDeviceList.DataBean.ListBean item) {
        helper.setText(R.id.tvDeviceName, item.getDeviceName());
        helper.setText(R.id.tvStatus, item.getDeviceStatus());

    }



}


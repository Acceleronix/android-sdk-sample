package com.acceleronix.app.demo.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.acceleronix.app.demo.R;
import com.acceleronix.app.demo.bean.SharedDevice;

import java.util.List;

public class SharedDeviceAdapter extends BaseQuickAdapter<SharedDevice, BaseViewHolder> {

    private Context mContext;

    public SharedDeviceAdapter(Context context, List data) {
        super(R.layout.shared_device_item, data);
        this.mContext  = context;
    }
    @Override
    protected void convert(BaseViewHolder helper, final SharedDevice item) {

        helper.setText(R.id.tv_pk,"pk: "+item.getPk());
        helper.setText(R.id.tv_dk,"dk: "+item.getDk());
        helper.setText(R.id.tv_dgid,"dgid: "+item.getDgid());
        helper.setText(R.id.tv_group_name,"groupName: "+item.getGroupName());
        helper.setText(R.id.tv_device_name,"deviceName: "+item.getDeviceName());

    }

}


package com.acceleronix.app.demo.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.acceleronix.app.demo.R;
import com.acceleronix.app.demo.bean.DeviceGroupVO;

import java.util.List;

public class DeviceGroupAdapter extends BaseQuickAdapter<DeviceGroupVO, BaseViewHolder> {

    private Context mContext;

    public DeviceGroupAdapter(Context context, List data) {
        super(R.layout.device_group_item, data);
        this.mContext  = context;
    }
    @Override
    protected void convert(BaseViewHolder helper, final DeviceGroupVO item) {

        helper.setText(R.id.tv_dgid,"dgid: "+item.getDgid());
        helper.setText(R.id.tv_group_name,"groupName: "+item.getName());

        int type =  item.getDeviceGroupType();
        if(type==1)
        {
            helper.setText(R.id.tv_group_type,"Self-grouping");
        }
        else
        {
            helper.setText(R.id.tv_group_type,"Accept other people's grouping");
        }

    }



}


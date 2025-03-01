package com.quectel.app.demo.adapter;

import android.view.View;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.quectel.app.demo.R;
import com.quectel.app.demo.bean.SmartConfigDevice;

public class SmartConfigDeviceAdapter extends BaseQuickAdapter<SmartConfigDevice, BaseViewHolder> {

    private OnStartConfigListener listener;

    public SmartConfigDeviceAdapter() {
        super(R.layout.quec_scene_item_device);
    }

    public void setListener(OnStartConfigListener listener) {
        if(listener!=null){
            this.listener = listener;
        }
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, SmartConfigDevice device) {
        int position = getItemPosition(device);
        baseViewHolder.setText(R.id.tvTitle, device.getDeviceBean().getDeviceName());
        baseViewHolder.getView(R.id.tv_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener!=null){
                    listener.onStartConfig(position,device);
                }
            }
        });
        if(device.getBindResult()==200){
            baseViewHolder.getView(R.id.tv_status).setVisibility(View.VISIBLE);
            baseViewHolder.getView(R.id.tv_start).setVisibility(View.GONE);
            baseViewHolder.setText(R.id.tv_status, "Network configuration successful");
        }else if(device.getBindResult()==300){
            baseViewHolder.getView(R.id.tv_status).setVisibility(View.VISIBLE);
            baseViewHolder.getView(R.id.tv_start).setVisibility(View.GONE);
            baseViewHolder.setText(R.id.tv_status, "Network configuration failed");
        }else if(device.getBindResult()==100){
            baseViewHolder.getView(R.id.tv_status).setVisibility(View.VISIBLE);
            baseViewHolder.setText(R.id.tv_status, "Binding…");
            baseViewHolder.getView(R.id.tv_start).setVisibility(View.GONE);
        }else {
            baseViewHolder.getView(R.id.tv_status).setVisibility(View.GONE);
            baseViewHolder.getView(R.id.tv_start).setVisibility(View.VISIBLE);
        }


//        baseViewHolder.setText(R.id.tv_status,deviceInfo.getDeviceKey());

    }

    public interface OnStartConfigListener {
        void onStartConfig(int position,SmartConfigDevice device);
    }


}

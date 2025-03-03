package com.acceleronix.app.demo.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.acceleronix.app.demo.R;
import com.acceleronix.app.demo.bean.DeviceOtaModel;
import com.quectel.sdk.ota.upgrade.model.OtaUpgradeStatus;

import java.util.List;

public class DeviceOtaAdapter extends BaseQuickAdapter<DeviceOtaModel, BaseViewHolder> {

    private Context mContext;

    public DeviceOtaAdapter(Context context, List data) {
        super(R.layout.device_ota_item, data);
        this.mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, final DeviceOtaModel waitUpgradeDevice) {
        helper.setText(R.id.tv_device_name, waitUpgradeDevice.getDeviceName());
        helper.setText(R.id.tv_version, "Version：" + waitUpgradeDevice.getVersion());
        helper.setText(R.id.tv_desc, "Describe：" + waitUpgradeDevice.getDesc());

        String statusText = "";
        if (waitUpgradeDevice.getDeviceStatus() == OtaUpgradeStatus.UPGRADING
                || (waitUpgradeDevice.getUserConfirmStatus() == OtaUpgradeStatus.UPGRADING && waitUpgradeDevice.getDeviceStatus() == OtaUpgradeStatus.NOT_UPGRADE)) {
            statusText = "Status: Upgrading" + " progress：" + waitUpgradeDevice.getUpgradeProgress() * 100 + "%";
        } else if (waitUpgradeDevice.getDeviceStatus() == OtaUpgradeStatus.UPGRADE_SUCCESS) {
            statusText = "Status: Upgrade Successfully";

        } else if (waitUpgradeDevice.getDeviceStatus() == OtaUpgradeStatus.UPGRADE_FAILED_IN_NOT_UPGRADE) {
            statusText = "Status: Upgrade failed";
        } else if (waitUpgradeDevice.getDeviceStatus() == OtaUpgradeStatus.UPGRADE_FAILED) {
            statusText = "Status: Upgrade failed, please try again";
        } else {
            statusText = "Status: Not upgraded, please confirm the upgrade";
        }

        helper.setText(R.id.tv_status_text, statusText);
    }

}


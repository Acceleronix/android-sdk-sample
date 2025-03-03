package com.acceleronix.app.demo.ui

import android.app.Activity
import android.content.Context
import android.util.Log
import com.quectel.app.device.iot.IotChannelController
import com.quectel.basic.common.entity.QuecDeviceModel
import com.quectel.basic.common.entity.QuecResult
import com.quectel.sdk.iot.channel.kit.callback.IotResultCallback
import com.quectel.sdk.iot.channel.kit.chanel.IQuecChannelManager
import com.quectel.sdk.iot.channel.kit.constaint.QuecIotChannelType
import com.quectel.sdk.iot.channel.kit.constaint.QuecIotDataSendMode
import com.quectel.sdk.iot.channel.kit.model.QuecIotDataPointsModel

class DeviceControlManager(
    val context: Context,
    var channelId: String,
    var onConnectCallback: (connected: Boolean, type: QuecIotChannelType) -> Unit,
    var onDataCallback: (channelId: String, type: QuecIotChannelType, module: QuecIotDataPointsModel) -> Unit,
    var onDisconnect: (channelId: String, type: QuecIotChannelType) -> Unit
) {

    public lateinit var mode: QuecIotDataSendMode;

    val listener = object : IQuecChannelManager.IQuecCallBackListener {
        override fun onConnect(p0: String, p1: QuecIotChannelType, p2: Boolean, p3: String?) {
            val activity = context as Activity
            activity.runOnUiThread {
                onConnectCallback(p2, p1)
            }

        }

        override fun onData(
            p0: String,
            p1: QuecIotChannelType,
            p2: QuecIotDataPointsModel
        ) {
            val activity = context as Activity
            activity.runOnUiThread {
                onDataCallback(p0, p1, p2);
            }

        }

        override fun onBleClose(p0: String?) {

        }

        override fun onDisConnect(p0: String, p1: QuecIotChannelType) {
            val activity = context as Activity
            activity.runOnUiThread {
                onDisconnect(p0, p1);
            }
        }

    }

    public fun startChannel(pkDkModel: QuecDeviceModel) {
        Log.e("DeviceControlManager", "send connect")
        val list = mutableListOf<QuecDeviceModel>()
        list.add(pkDkModel);
        mode = QuecIotDataSendMode.QuecIotDataSendModeAuto
        IotChannelController.getInstance().startChannels(context, list, listener)
    }


    fun disConnect(pkDkModel: QuecDeviceModel) {
        IotChannelController.getInstance().closeChannel(pkDkModel.dk + "_" + pkDkModel.pk,
            QuecIotChannelType.QuecIotChannelTypeWifi.`val`)
        IotChannelController.getInstance().closeChannel(pkDkModel.dk + "_" + pkDkModel.pk,
            QuecIotChannelType.QuecIotChannelTypeBLE.`val`)
    }

    public fun startChannel(pkDkModel: QuecDeviceModel, mode: QuecIotDataSendMode) {
        Log.e("DeviceControlManager", "send connect with mode $mode")
        val list = mutableListOf<QuecDeviceModel>()
        list.add(pkDkModel);
        IotChannelController.getInstance().setListener(listener)
        this.mode = mode;
        IotChannelController.getInstance().startChannel(context, pkDkModel, mode)
    }

    fun writeDps(dataPointsModel: MutableList<QuecIotDataPointsModel.DataModel<Any>>) {

        IotChannelController.getInstance().writeDps(channelId, dataPointsModel, QuecIotDataSendMode.QuecIotDataSendModeAuto, null, object : IotResultCallback<Unit?>{
            override fun onFail(result: QuecResult<Unit>) {
                Log.e("DeviceControlManager", "send data failed")
            }

            override fun onSuccess(t: Unit?) {
                Log.e("DeviceControlManager", "send data success")
            }

        })

    }


}
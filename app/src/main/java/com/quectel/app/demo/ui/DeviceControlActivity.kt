package com.quectel.app.demo.ui

import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.OnClick
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.quectel.app.demo.R
import com.quectel.app.demo.adapter.DeviceModelAdapter
import com.quectel.app.demo.base.BaseActivity
import com.quectel.app.demo.bean.UserDeviceList.DataBean.ListBean
import com.quectel.app.demo.utils.AddOperate
import com.quectel.app.demo.utils.MyUtils
import com.quectel.app.demo.utils.ToastUtils
import com.quectel.app.demo.widget.BottomItemDecorationSystem
import com.quectel.app.device.bean.ArraySpecs
import com.quectel.app.device.bean.ArrayStructSpecs
import com.quectel.app.device.bean.BatchControlDevice
import com.quectel.app.device.bean.BooleanSpecs
import com.quectel.app.device.bean.BusinessValue
import com.quectel.app.device.bean.ModelBasic
import com.quectel.app.device.bean.NumSpecs
import com.quectel.app.device.bean.TSLEvent
import com.quectel.app.device.bean.TSLService
import com.quectel.app.device.bean.TextSpecs
import com.quectel.app.device.callback.IDeviceTSLCallBack
import com.quectel.app.device.constant.ModelStyleConstant
import com.quectel.app.device.deviceservice.IDevService
import com.quectel.app.device.receiver.NetStatusReceiver
import com.quectel.app.device.utils.DeviceServiceFactory
import com.quectel.app.quecnetwork.httpservice.IHttpCallBack
import com.quectel.app.websocket.websocket.cmd.KValue
import com.quectel.basic.common.entity.QuecDeviceModel
import com.quectel.basic.common.utils.QuecGsonUtil
import com.quectel.basic.common.utils.QuecThreadUtil
import com.quectel.basic.common.utils.QuecToastUtil
import com.quectel.sdk.iot.channel.kit.constaint.QuecIotChannelType
import com.quectel.sdk.iot.channel.kit.constaint.QuecIotDataSendMode
import com.quectel.sdk.iot.channel.kit.model.QuecIotDataPointsModel
import com.quectel.sdk.iot.channel.kit.model.QuecIotDataPointsModel.DataModel.QuecIotDataPointDataType
import com.suke.widget.SwitchButton
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.Date


class DeviceControlActivity() : BaseActivity() {


    var pk: String? = ""
    var dk: String? = ""

    var mReceiver: NetStatusReceiver? = null


    val map = mapOf(
        QuecIotDataPointDataType.BOOL to QuecIotDataPointDataType.BOOL_NUM,
        QuecIotDataPointDataType.DATE to QuecIotDataPointDataType.DATE_NUM,
        QuecIotDataPointDataType.DOUBLE to QuecIotDataPointDataType.DOUBLE_NUM,
        QuecIotDataPointDataType.FLOAT to QuecIotDataPointDataType.FLOAT_NUM,
        QuecIotDataPointDataType.ARRAY to QuecIotDataPointDataType.ARRAY_NUM,
        QuecIotDataPointDataType.ENUM to QuecIotDataPointDataType.ENUM_NUM,
        QuecIotDataPointDataType.INT to QuecIotDataPointDataType.INT_NUM,
        QuecIotDataPointDataType.TEXT to QuecIotDataPointDataType.TEXT_NUM,
        QuecIotDataPointDataType.STRUCT to QuecIotDataPointDataType.STRUCT_NUM
    )

    var mode: QuecIotDataSendMode = QuecIotDataSendMode.QuecIotDataSendModeAuto;

    var isOnline = true
    var readList: MutableList<BusinessValue?> = ArrayList()
    var readWriteList: MutableList<BusinessValue?> = ArrayList()
    var contentList: MutableList<BusinessValue?> = ArrayList()
    var mAdapter: DeviceModelAdapter? = null
    var booleanData: KValue? = null
    var cachePosition = -1
    var cacheMap = HashMap<Int, View>()
    var numberCacheMap = HashMap<Int, BusinessValue>()

    lateinit var device: ListBean
    lateinit var radio: RadioGroup

    override fun getContentLayout(): Int {
        return R.layout.activity_device_control
    }

    override fun addHeadColor() {
        MyUtils.addStatusBarView(this, R.color.gray_bg)
    }

    lateinit var recyclerView: RecyclerView

    lateinit var tvConnect: TextView;

    lateinit var ivBack: ImageView

    var pkDkModle: QuecDeviceModel = QuecDeviceModel();
    var deviceControlManager: DeviceControlManager? = null

    val onConnectCallback = { it: Boolean, type: QuecIotChannelType ->
        QuecThreadUtil.RunMainThread {
            tvConnect.text = "isConnect $it type ${type.`val`}"
        }
        Toast.makeText(activity, "isConnect $it type ${type.`val`}", Toast.LENGTH_SHORT).show()

    }

    val onDataCallback = { channelId: String,
                           type: QuecIotChannelType,
                           module: QuecIotDataPointsModel ->
        Log.e("onDataCallback", QuecGsonUtil.gsonString(module))
        val jsonObject = if (module.rawData == null) module.dps else module.rawData
        Toast.makeText(activity, "onDataCallback ${jsonObject.toString()}", Toast.LENGTH_SHORT)
            .show()

    }

    val onDisconnect = { channelId: String, type: QuecIotChannelType ->
        QuecThreadUtil.RunMainThread {
            tvConnect.text = "onDisConnect $channelId ${type.`val`}"
        }
        Toast.makeText(activity, "onDisConnect $channelId ${type.`val`}", Toast.LENGTH_SHORT).show()
    }


    override fun initData() {

        ivBack = findViewById(R.id.iv_back)
        ivBack.setOnClickListener {
            finish()
        }
        recyclerView = findViewById(R.id.mList);
        tvConnect = findViewById(R.id.tv_connect)
        mReceiver = NetStatusReceiver()
        val filter = IntentFilter()
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(mReceiver, filter)
        val intent = intent
        device = intent.getSerializableExtra("device") as ListBean;
        pk = device.productKey
        dk = device.deviceKey

        isOnline = device.onlineStatus != 0
        queryModelTSL()

        val channelId: String = pk + "_" + dk
        deviceControlManager =
            DeviceControlManager(
                activity,
                channelId,
                onConnectCallback,
                onDataCallback,
                onDisconnect
            )
        val pkDkModle: QuecDeviceModel = QuecDeviceModel();
        pkDkModle.pk = pk;
        pkDkModle.dk = dk;
        pkDkModle.capabilitiesBitmask = device?.capabilitiesBitmask!!
        pkDkModle.onlineStatus = 1
        pkDkModle.bindingkey = device?.authKey

        deviceControlManager?.startChannel(pkDkModle)

        initAdapter()

        //Determine whether the device has WS capability
        val hasWsCapabilities = pkDkModle.capabilitiesBitmask and 1 != 0
        findViewById<View>(R.id.radio_ws).visibility =
            if (hasWsCapabilities) View.VISIBLE else View.GONE
        //Determine whether the device has Wi-Fi capability
        val hasWifiCapabilities = pkDkModle.capabilitiesBitmask shr 1 and 1 != 0
        findViewById<View>(R.id.radio_wifi).visibility =
            if (hasWifiCapabilities) View.VISIBLE else View.GONE
        //Determine whether the device has Ble capability
        val hasBleCapabilities = pkDkModle.capabilitiesBitmask shr 2 and 1 != 0
        findViewById<View>(R.id.radio_ble).visibility =
            if (hasBleCapabilities) View.VISIBLE else View.GONE

        radio = findViewById(R.id.radioGroup)
        radio.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                when (checkedId) {
                    R.id.radio_auto -> {
                        mode = QuecIotDataSendMode.QuecIotDataSendModeAuto
                    }

                    R.id.radio_wifi -> {
                        mode = QuecIotDataSendMode.QuecIotDataSendModeWifi
                    }

                    R.id.radio_ble -> {
                        mode = QuecIotDataSendMode.QuecIotDataSendModeBLE
                    }

                    R.id.radio_ws -> {
                        mode = QuecIotDataSendMode.QuecIotDataSendModeWS
                    }
                }
                deviceControlManager?.startChannel(pkDkModle, mode)
            }

        })

    }


    fun initAdapter() {
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.addItemDecoration(
            BottomItemDecorationSystem(
                activity
            )
        )
        mAdapter = DeviceModelAdapter(activity, contentList)
        recyclerView!!.adapter = mAdapter
        mAdapter!!.setOnItemClickListener { adapter, view, position ->
            println("position--:$position")
            if (isOnline) {
                val item = mAdapter!!.data[position]
                val type = item.dataType
                val subType = item.subType
                if (type == ModelStyleConstant.BOOL && subType.contains("W")) {
                    cachePosition = position
                    cacheMap[position] = view
                    val switch_button =
                        view.findViewById<SwitchButton>(R.id.switch_button)
                    switch_button.setOnCheckedChangeListener { view, isChecked ->
                        println("isChecked--:$isChecked")
                        val dp = QuecIotDataPointsModel.DataModel<Any>();
                        dp.id = item.abId;
                        dp.dataType = map[item.dataType.uppercase()]
                        dp.value = isChecked.toString()
                        deviceControlManager?.writeDps(mutableListOf(dp))
                    }

                    switch_button.toggle()
                } else if (subType.contains("W")) {
                    if (type == ModelStyleConstant.INT || type == ModelStyleConstant.FLOAT || type == ModelStyleConstant.DOUBLE) {
                        cachePosition = position
                        var step: String? = null
                        val code = item.resourceCode
                        for (mb in modelBasics!!) {
                            if (code == mb.getCode()) {
                                val numSpecs = mb.getSpecs()[0] as NumSpecs
                                step =
                                    "min:" + numSpecs.min + " max:" + numSpecs.max + " step:" + numSpecs.step
                                createSendDialog(numSpecs, step, item)
                            }
                        }
                    } else if (type == ModelStyleConstant.ENUM) {
                        cachePosition = position
                        val code = item.resourceCode
                        for (mb in modelBasics!!) {
                            if (code == mb.getCode()) {
                                val specs: List<BooleanSpecs> =
                                    mb.getSpecs() as List<BooleanSpecs>
                                createSendEnumDialog(specs, item)
                            }
                        }
                    } else if (type == ModelStyleConstant.DATE || type == ModelStyleConstant.TEXT) {
                        cachePosition = position
                        val code = item.resourceCode
                        for (mb in modelBasics!!) {
                            if (code == mb.getCode()) {
                                if (type == ModelStyleConstant.TEXT) {
                                    val ts = mb.getSpecs()[0] as TextSpecs
                                    createDateOrTextDialog(ts, item)
                                } else {
                                    createDateOrTextDialog(null, item)
                                }
                            }
                        }
                    } else if (type == ModelStyleConstant.STRUCT) {
                        cachePosition = position
                        val code = item.resourceCode
                        for (mb in modelBasics!!) {
                            if (code == mb.getCode()) {
                                val specs: List<ModelBasic<*>> =
                                    mb.getSpecs() as List<ModelBasic<*>>
                                createSendStructDialog(specs, item)
                            }
                        }
                    } else if (type == ModelStyleConstant.ARRAY) {
                        cachePosition = position
                        val code = item.resourceCode
                        for (mb in modelBasics!!) {
                            if (code == mb.getCode()) {
                                val obj = mb.getSpecs()[0]
                                  if (obj is ArrayStructSpecs<*>) {
                                    createSendArrayContainStructDialog(obj, item)
                                } else if (obj is ArraySpecs) {
                                      createSendSimpleArrayDialog(obj, item)
                                }
                            }
                        }
                    }
                }
            } else {
                val item = mAdapter!!.data[position]
                val type = item.dataType
                val subType = item.subType
                if (type == ModelStyleConstant.BOOL && subType == "RW") {
                    cachePosition = position
                    cacheMap[position] = view
                    val switch_button =
                        view.findViewById<SwitchButton>(R.id.switch_button)
                    switch_button.setOnCheckedChangeListener { view, isChecked ->
                        try {
                            val obj = JSONObject()
                            obj.put(item.resourceCode, isChecked.toString())
                            val data = JSONArray().put(obj).toString()
                            sendBaseHttpData(data, pk, dk)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    switch_button.toggle()
                } else if (subType == "RW") {
                    if (type == ModelStyleConstant.INT || type == ModelStyleConstant.FLOAT || type == ModelStyleConstant.DOUBLE) {
                        cachePosition = position
                        var step: String? = null
                        val code = item.resourceCode
                        for (mb in modelBasics!!) {
                            if (code == mb.getCode()) {
                                val numSpecs = mb.getSpecs()[0] as NumSpecs
                                step =
                                    "min:" + numSpecs.min + " max:" + numSpecs.max + " step:" + numSpecs.step
                                createSendDialog(numSpecs, step, item)
                            }
                        }
                    } else if (type == ModelStyleConstant.ENUM) {
                        cachePosition = position
                        val code = item.resourceCode
                        for (mb in modelBasics!!) {
                            if (code == mb.getCode()) {
                                val specs: List<BooleanSpecs> =
                                    mb.getSpecs() as List<BooleanSpecs>
                                createSendEnumDialog(specs, item)
                            }
                        }
                    } else if (type == ModelStyleConstant.DATE || type == ModelStyleConstant.TEXT) {
                        cachePosition = position
                        val code = item.resourceCode
                        for (mb in modelBasics!!) {
                            if (code == mb.getCode()) {
                                if (type == ModelStyleConstant.TEXT) {
                                    val ts = mb.getSpecs()[0] as TextSpecs
                                    createDateOrTextDialog(ts, item)
                                } else {
                                    createDateOrTextDialog(null, item)
                                }
                            }
                        }
                    } else if (type == ModelStyleConstant.STRUCT) {
                        cachePosition = position
                        val code = item.resourceCode
                        for (mb in modelBasics!!) {
                            if (code == mb.getCode()) {
                                val specs: List<ModelBasic<*>> =
                                    mb.getSpecs() as List<ModelBasic<*>>
                                createSendStructDialog(specs, item)
                            }
                        }
                    } else if (type == ModelStyleConstant.ARRAY) {
                        cachePosition = position
                        val code = item.resourceCode
                        for (mb in modelBasics!!) {
                            if (code == mb.getCode()) {
                                val obj = mb.getSpecs()[0]
                                if (obj is ArrayStructSpecs<*>) {
                                    createSendArrayContainStructDialog(obj, item)
                                } else if (obj is ArraySpecs) {
                                    createSendSimpleArrayDialog(obj, item)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @OnClick(R.id.iv_back)
    fun onViewClick(view: View) {
        val intent: Intent? = null
        when (view.id) {
            R.id.iv_back -> finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        WebSocketServiceLocater.getService(IWebSocketService::class.java).disconnect()
        if (mReceiver != null) {
            unregisterReceiver(mReceiver)
        }
        deviceControlManager?.disConnect(pkDkModle)
    }

    var modelBasics: List<ModelBasic<Any>>? = null
    private fun queryModelTSL() {

        //Query the object model. If you need to query the object model and attributes at the same time, you can useDeviceServiceFactory.getInstance().getService(IDevService::class.java) .getProductTSLValueWithProductKey
        DeviceServiceFactory.getInstance().getService(IDevService::class.java)
            .getProductTSLWithCache(pk, object : IDeviceTSLCallBack {
                override fun onSuccess(
                    modelBasicList: MutableList<ModelBasic<Any>>?,
                    tslEventList: MutableList<TSLEvent>?,
                    tslServiceList: MutableList<TSLService>?
                ) {
                    modelBasics = modelBasicList
                    val list = covertBussinevalue(modelBasics)
                    val writeList = list.filter { !it.subType.equals("R") }
                    contentList.clear()
                    contentList.addAll(writeList)
                    mAdapter?.notifyDataSetChanged()
                }

                override fun onFail(throwable: Throwable?) {
                    throwable?.printStackTrace()
                }
            })

    }

    private fun createSendDialog(numSpecs: NumSpecs, step: String, item: BusinessValue) {
        numberCacheMap[cachePosition] = item
        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.send_model_command_dialog, null)
        val mDialog = Dialog(activity, R.style.dialogTM)
        mDialog.setContentView(view)
        mDialog.setCancelable(true)
        mDialog.setCanceledOnTouchOutside(false)
        val edit_content = mDialog.findViewById<View>(R.id.edit_content) as EditText
        edit_content.setText(item.resourceValce)
        val tv_step = mDialog.findViewById<View>(R.id.tv_step) as TextView
        val bt_cancel = mDialog.findViewById<View>(R.id.bt_cancel) as Button
        val bt_sure = mDialog.findViewById<View>(R.id.bt_sure) as Button
        val bt_sub = mDialog.findViewById<View>(R.id.bt_sub) as Button
        val bt_add = mDialog.findViewById<View>(R.id.bt_add) as Button
        tv_step.text = step
        bt_cancel.setOnClickListener { mDialog.dismiss() }
        bt_sure.setOnClickListener {
            mDialog.dismiss()
            if (isOnline) {
                val value = MyUtils.getEditTextContent(edit_content)
                item.resourceValce = value
                sendDps(item, value)
                contentList[cachePosition] = item
                mAdapter!!.notifyDataSetChanged()

//                sendDps(item, item.resourceCode)
//                contentList[cachePosition] = item
//                mAdapter!!.notifyDataSetChanged()
            } else {
                try {
                    val obj = JSONObject()
                    obj.put(item.resourceCode, item.resourceValce)
                    val data = JSONArray().put(obj).toString()
                    sendBaseHttpData(data, pk, dk)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        bt_sub.setOnClickListener {
            item.resourceValce = edit_content.text.toString()
            if (TextUtils.isEmpty(item.resourceValce)) {
                QuecToastUtil.showL("Please enter a value")
                return@setOnClickListener
            }
            if (item.resourceValce.toInt() <= 0) {
                return@setOnClickListener
            }
            val result = AddOperate.sub(item.resourceValce, numSpecs.step)
            item.resourceValce = result
            edit_content.setText(result)
        }
        bt_add.setOnClickListener {
            item.resourceValce = edit_content.text.toString()
            if (TextUtils.isEmpty(item.resourceValce)) {
                QuecToastUtil.showL("Please enter a value")
                return@setOnClickListener
            }
            val result = AddOperate.add(item.resourceValce, numSpecs.step)
            item.resourceValce = result
            edit_content.setText(result)
        }
        mDialog.show()
    }

    private fun createSendEnumDialog(specs: List<BooleanSpecs>, item: BusinessValue) {
        numberCacheMap[cachePosition] = item
        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.send_model_enum_dialog, null)
        val mDialog = Dialog(activity, R.style.dialogTM)
        mDialog.setContentView(view)
        mDialog.setCancelable(true)
        mDialog.setCanceledOnTouchOutside(false)
        val edit_value = mDialog.findViewById<View>(R.id.edit_value) as EditText
        val tv_enum_name = mDialog.findViewById<View>(R.id.tv_enum_name) as TextView
        val tv_enum_value = mDialog.findViewById<View>(R.id.tv_enum_value) as TextView
        val bt_cancel = mDialog.findViewById<View>(R.id.bt_cancel) as Button
        val bt_sure = mDialog.findViewById<View>(R.id.bt_sure) as Button
        val sb1 = StringBuilder()
        val sb2 = StringBuilder()
        for (bp in specs) {
            sb1.append(bp.name)
            sb1.append(" ")
            sb2.append(bp.value)
            sb2.append(" ")
        }
        tv_enum_name.text = "Enum name: $sb1"
        tv_enum_value.text = "Enum value: $sb2"
        bt_cancel.setOnClickListener { mDialog.dismiss() }
        bt_sure.setOnClickListener {
            mDialog.dismiss()
            if (isOnline) {
                val enumValue = MyUtils.getEditTextContent(edit_value)
                item.resourceValce = enumValue
                sendDps(item, enumValue)
                contentList[cachePosition] = item
                mAdapter!!.notifyDataSetChanged()
            } else {
                val enumValue = MyUtils.getEditTextContent(edit_value)
                try {
                    val obj = JSONObject()
                    obj.put(item.resourceCode, enumValue)
                    val data = JSONArray().put(obj).toString()
                    sendBaseHttpData(data, pk, dk)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        mDialog.show()
    }

    private fun createDateOrTextDialog(specs: TextSpecs?, item: BusinessValue) {
        numberCacheMap[cachePosition] = item
        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.send_model_date_text_dialog, null)
        val mDialog = Dialog(activity, R.style.dialogTM)
        mDialog.setContentView(view)
        mDialog.setCancelable(true)
        mDialog.setCanceledOnTouchOutside(false)
        val edit_value = mDialog.findViewById<View>(R.id.edit_value) as EditText
        edit_value.setText(item.resourceValce)
        val tv_text_length = mDialog.findViewById<View>(R.id.tv_text_length) as TextView
        if (specs != null) {
            tv_text_length.text = "Text length: " + specs.length
        } else {
            tv_text_length.visibility = View.GONE
        }
        val bt_cancel = mDialog.findViewById<View>(R.id.bt_cancel) as Button
        val bt_sure = mDialog.findViewById<View>(R.id.bt_sure) as Button
        bt_cancel.setOnClickListener { mDialog.dismiss() }
        bt_sure.setOnClickListener {
            mDialog.dismiss()
            if (isOnline) {
                val value = MyUtils.getEditTextContent(edit_value)

                item.resourceValce = value

                sendDps(item, value)
                contentList[cachePosition] = item
                mAdapter!!.notifyDataSetChanged()
            } else {
                val enumValue = MyUtils.getEditTextContent(edit_value)
                try {
                    val obj = JSONObject()
                    obj.put(item.resourceCode, enumValue)
                    val data = JSONArray().put(obj).toString()
                    sendBaseHttpData(data, pk, dk)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        mDialog.show()
    }

    //下发结构体数据
    private fun createSendStructDialog(specs: List<ModelBasic<*>>?, item: BusinessValue) {
        numberCacheMap[cachePosition] = item
        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.send_model_struct_dialog, null)
        val mDialog = Dialog(activity, R.style.dialogTM)
        mDialog.setContentView(view)
        mDialog.setCancelable(true)
        mDialog.setCanceledOnTouchOutside(false)
        val bt_cancel = mDialog.findViewById<View>(R.id.bt_cancel) as Button
        val bt_sure = mDialog.findViewById<View>(R.id.bt_sure) as Button
        bt_cancel.setOnClickListener { mDialog.dismiss() }
        bt_sure.setOnClickListener {
            mDialog.dismiss()
            //    * attribute array
            //    * "[{\"key\":[{\"id\":\"value1\"},{\"id\":\"value2\"}]}]"（id: 0）
            //    * attribute struct
            //    * "[{\"key\":[{\"key1\":\"value1\"},{\"key2\":\"value2\"}]}]"
            //    * attribute array contain struct
            //    * "[{\"key\":[{\"id\":[{\"key1\":\"value1\"}]},{\"id\":[{\"key2\":\"value2\"}]}]}]"（id: 0）
            if (specs != null && specs.size > 0) {
                if (isOnline) {
                    val mListChild: MutableList<QuecIotDataPointsModel.DataModel<Any>> = ArrayList()
                    for (mb in specs) {
                        if (mb.getDataType() == ModelStyleConstant.BOOL) {

                            val data = QuecIotDataPointsModel.DataModel<Any>();
                            data.id = mb.id
                            data.code = mb.getCode();
                            data.dataType = map[mb.getDataType()]
                            data.value = true
//                            val v1 =
//                                KValue(mb.getId(), mb.getName(), ModelStyleConstant.BOOL, "true")
                            mListChild.add(data)
                        } else if (mb.getDataType() == ModelStyleConstant.INT) {
                            //val v1 = KValue(mb.getId(), mb.getName(), ModelStyleConstant.INT, 55)
                            val data = QuecIotDataPointsModel.DataModel<Any>();
                            data.id = mb.id
                            data.code = mb.getCode();
                            data.dataType = map[mb.getDataType()]
                            data.value = 55
                            mListChild.add(data)
                        } else if (mb.getDataType() == ModelStyleConstant.ENUM) {
                            val specs: List<BooleanSpecs> = mb.getSpecs() as List<BooleanSpecs>
                            for (bs in specs) {
                                println("bs--:" + bs.getValue())
                            }
                            val data = QuecIotDataPointsModel.DataModel<Any>();
                            data.id = mb.id
                            data.code = mb.getCode();
                            data.dataType = map[mb.getDataType()]
                            data.value = specs[0].getValue()
                            mListChild.add(data)
                        } else if (mb.getDataType() == ModelStyleConstant.FLOAT) {
                            val data = QuecIotDataPointsModel.DataModel<Any>();
                            data.id = mb.id
                            data.code = mb.getCode();
                            data.dataType = map[mb.getDataType()]
                            data.value = "22.22"
                            mListChild.add(data)
                        } else if (mb.getDataType() == ModelStyleConstant.DOUBLE) {
                            val data = QuecIotDataPointsModel.DataModel<Any>();
                            data.id = mb.id
                            data.code = mb.getCode();
                            data.dataType = map[mb.getDataType()]
                            data.value = "33.33"
                            mListChild.add(data)
                        } else if (mb.getDataType() == ModelStyleConstant.TEXT) {
                            val data = QuecIotDataPointsModel.DataModel<Any>();
                            data.id = mb.id
                            data.code = mb.getCode();
                            data.dataType = map[mb.getDataType()]
                            data.value = "text_content"
                            mListChild.add(data)
                        } else if (mb.getDataType() == ModelStyleConstant.DATE) {
                            val data = QuecIotDataPointsModel.DataModel<Any>();
                            data.id = mb.id
                            data.code = mb.getCode();
                            data.dataType = map[mb.getDataType()]
                            data.value = Date().time.toString()
                            mListChild.add(data)
                        }
                    }

                    val structData = QuecIotDataPointsModel.DataModel<Any>();
                    structData.id = item.abId
                    structData.code = item.resourceCode
                    structData.dataType = map[item.dataType]
                    structData.value = mListChild
                    deviceControlManager?.writeDps(mutableListOf(structData))
                } else {
                    // * "[{\"key\":[{\"key1\":\"value1\"},{\"key2\":\"value2\"}]}]"
                    try {
                        val obj = JSONObject()
                        val childArray = JSONArray()
                        for (mb in specs) {
                            if (mb.getDataType() == ModelStyleConstant.BOOL) {
                                val child1 = JSONObject()
                                child1.put(mb.getCode(), "true")
                                childArray.put(child1)
                            } else if (mb.getDataType() == ModelStyleConstant.INT) {
                                val child1 = JSONObject()
                                child1.put(mb.getCode(), 88)
                                childArray.put(child1)
                            } else if (mb.getDataType() == ModelStyleConstant.ENUM) {
                                val specs: List<BooleanSpecs> = mb.getSpecs() as List<BooleanSpecs>
                                val child1 = JSONObject()
                                child1.put(mb.getCode(), specs[0].getValue())
                                childArray.put(child1)
                            } else if (mb.getDataType() == ModelStyleConstant.FLOAT) {
                                val child1 = JSONObject()
                                child1.put(mb.getCode(), 12.2)
                                childArray.put(child1)
                            } else if (mb.getDataType() == ModelStyleConstant.DOUBLE) {
                                val child1 = JSONObject()
                                child1.put(mb.getCode(), 12.3)
                                childArray.put(child1)
                            } else if (mb.getDataType() == ModelStyleConstant.TEXT) {
                                val child1 = JSONObject()
                                child1.put(mb.getCode(), "test_content")
                                childArray.put(child1)
                            } else if (mb.getDataType() == ModelStyleConstant.DATE) {
                                val child1 = JSONObject()
                                child1.put(mb.getCode(), Date().time.toString())
                                childArray.put(child1)
                            }
                        }
                        obj.put(item.resourceCode, childArray)
                        val data = JSONArray().put(obj).toString()
                        sendBaseHttpData(data, pk, dk)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        mDialog.show()
    }

    //Sending array contains basic type data
    private fun createSendSimpleArrayDialog(specs: ArraySpecs?, item: BusinessValue) {
        numberCacheMap[cachePosition] = item
        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.send_model_struct_dialog, null)
        val mDialog = Dialog(activity, R.style.dialogTM)
        mDialog.setContentView(view)
        mDialog.setCancelable(true)
        mDialog.setCanceledOnTouchOutside(false)
        val bt_cancel = mDialog.findViewById<View>(R.id.bt_cancel) as Button
        val bt_sure = mDialog.findViewById<View>(R.id.bt_sure) as Button
        bt_cancel.setOnClickListener { mDialog.dismiss() }
        bt_sure.setOnClickListener {
            mDialog.dismiss()
            if (specs != null) {
                if (isOnline) {
                    val mListChild: MutableList<QuecIotDataPointsModel.DataModel<Any>> = ArrayList()
                    //Determine what type of data is added to the array based on getDataType. The added data cannot exceed specs.getSize()
                    val mb = specs
                    if (mb.getDataType() == ModelStyleConstant.BOOL) {
                        val data = QuecIotDataPointsModel.DataModel<Any>();
                        data.dataType = map[mb.getDataType()]
                        data.value = true
                        mListChild.add(data)
                    } else if (mb.getDataType() == ModelStyleConstant.INT) {
                        //val v1 = KValue(mb.getId(), mb.getName(), ModelStyleConstant.INT, 55)
                        val data = QuecIotDataPointsModel.DataModel<Any>();
                        data.dataType = map[mb.getDataType()]
                        data.value = 8
                        mListChild.add(data)

                    } else if (mb.getDataType() == ModelStyleConstant.FLOAT) {
                        val data = QuecIotDataPointsModel.DataModel<Any>();
                        data.dataType = map[mb.getDataType()]
                        data.value = "22.22"
                        mListChild.add(data)
                    } else if (mb.getDataType() == ModelStyleConstant.DOUBLE) {
                        val data = QuecIotDataPointsModel.DataModel<Any>();
                        data.dataType = map[mb.getDataType()]
                        data.value = "33.33"
                        mListChild.add(data)
                    } else if (mb.getDataType() == ModelStyleConstant.TEXT) {
                        val data = QuecIotDataPointsModel.DataModel<Any>();

                        data.dataType = map[mb.getDataType()]
                        data.value = "text_content"
                        mListChild.add(data)
                    } else if (mb.getDataType() == ModelStyleConstant.DATE) {
                        val data = QuecIotDataPointsModel.DataModel<Any>();
                        data.dataType = map[mb.getDataType()]
                        data.value = Date().time.toString()
                        mListChild.add(data)
                    }

                    val arrayData = QuecIotDataPointsModel.DataModel<Any>();
                    arrayData.id = item.abId
                    arrayData.code = item.resourceCode
                    arrayData.dataType = map[item.dataType]
                    arrayData.value = mListChild
                    deviceControlManager?.writeDps(mutableListOf(arrayData))

                } else {
                    //"[{\"key\":[{\"id\":\"value1\"},{\"id\":\"value2\"}]}]"（id: 0）
                    try {
                        val obj = JSONObject()
                        val childArray = JSONArray()
                        if (specs.getDataType() == ModelStyleConstant.INT) {
                            val child1 = JSONObject()
                            child1.put("0", "77")
                            childArray.put(child1)
                        } else if (specs.getDataType() == ModelStyleConstant.BOOL) {
                            val child1 = JSONObject()
                            child1.put("0", "false")
                            childArray.put(child1)
                        } else if (specs.getDataType() == ModelStyleConstant.ENUM) {
                            val child1 = JSONObject()
                            child1.put("0", 1)
                            childArray.put(child1)
                        } else if (specs.getDataType() == ModelStyleConstant.FLOAT) {
                            val child1 = JSONObject()
                            child1.put("0", 2.3)
                            childArray.put(child1)
                        } else if (specs.getDataType() == ModelStyleConstant.DOUBLE) {
                            val child1 = JSONObject()
                            child1.put("0", 3.5)
                            childArray.put(child1)
                        } else if (specs.getDataType() == ModelStyleConstant.TEXT) {
                            val child1 = JSONObject()
                            child1.put("0", "text")
                            childArray.put(child1)
                        } else if (specs.getDataType() == ModelStyleConstant.DATE) {
                            val child1 = JSONObject()
                            child1.put("0", Date().time.toString())
                            childArray.put(child1)
                        }
                        obj.put(item.resourceCode, childArray)
                        val data = JSONArray().put(obj).toString()
                        sendBaseHttpData(data, pk, dk)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        mDialog.show()
    }

    //Sending an array of nested structures
    private fun createSendArrayContainStructDialog(
        specs: ArrayStructSpecs<*>?,
        item: BusinessValue
    ) {
        numberCacheMap[cachePosition] = item
        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.send_model_struct_dialog, null)
        val mDialog = Dialog(activity, R.style.dialogTM)
        mDialog.setContentView(view)
        mDialog.setCancelable(true)
        mDialog.setCanceledOnTouchOutside(false)
        val bt_cancel = mDialog.findViewById<View>(R.id.bt_cancel) as Button
        val bt_sure = mDialog.findViewById<View>(R.id.bt_sure) as Button
        bt_cancel.setOnClickListener { mDialog.dismiss() }
        bt_sure.setOnClickListener {
            mDialog.dismiss()
            if (specs != null) {
                if (isOnline) {
                    val specs1 = specs.getSpecs()
                    val childList1: MutableList<QuecIotDataPointsModel.DataModel<Any>> = ArrayList()
                    val childList2:  MutableList<QuecIotDataPointsModel.DataModel<Any>> = ArrayList()
                    for (mb in specs1) {
                        if (mb.getDataType() == ModelStyleConstant.BOOL) {
                            val data1 = QuecIotDataPointsModel.DataModel<Any>()
                            data1.id = mb.id
                            data1.code = mb.getCode()
                            data1.dataType = map[mb.getDataType()]
                            data1.value = true
                            childList1.add(data1)

                            val data2 = QuecIotDataPointsModel.DataModel<Any>()
                            data2.id = mb.id
                            data2.code = mb.getCode()
                            data2.dataType = map[mb.getDataType()]
                            data2.value = false
                            childList2.add(data2)
                        } else if (mb.getDataType() == ModelStyleConstant.ENUM) {
                            val specs: List<BooleanSpecs> = mb.getSpecs() as List<BooleanSpecs>
                            val data1 = QuecIotDataPointsModel.DataModel<Any>()
                            data1.id = mb.id
                            data1.code = mb.getCode()
                            data1.dataType = map[mb.getDataType()]
                            data1.value = specs[0].getValue()
                            childList1.add(data1)

                            val data2 = QuecIotDataPointsModel.DataModel<Any>()
                            data2.id = mb.id
                            data2.code = mb.getCode()
                            data2.dataType = map[mb.getDataType()]
                            data2.value = specs[0].getValue()
                            childList2.add(data2)
                        } else if (mb.getDataType() == ModelStyleConstant.INT) {
                            val data1 = QuecIotDataPointsModel.DataModel<Any>()
                            data1.id = mb.id
                            data1.code = mb.getCode()
                            data1.dataType = map[mb.getDataType()]
                            data1.value = 5
                            childList1.add(data1)

                            val data2 = QuecIotDataPointsModel.DataModel<Any>()
                            data2.id = mb.id
                            data2.code = mb.getCode()
                            data2.dataType = map[mb.getDataType()]
                            data2.value = 6
                            childList2.add(data2)

                        } else if (mb.getDataType() == ModelStyleConstant.FLOAT) {
                            val data1 = QuecIotDataPointsModel.DataModel<Any>()
                            data1.id = mb.id
                            data1.code = mb.getCode()
                            data1.dataType = map[mb.getDataType()]
                            data1.value = "25.22"
                            childList1.add(data1)

                            val data2 = QuecIotDataPointsModel.DataModel<Any>()
                            data2.id = mb.id
                            data2.code = mb.getCode()
                            data2.dataType = map[mb.getDataType()]
                            data2.value = "26.33"
                            childList2.add(data2)

                        } else if (mb.getDataType() == ModelStyleConstant.DOUBLE) {
                            val data1 = QuecIotDataPointsModel.DataModel<Any>()
                            data1.id = mb.id
                            data1.code = mb.getCode()
                            data1.dataType = map[mb.getDataType()]
                            data1.value = "5.442"
                            childList1.add(data1)

                            val data2 = QuecIotDataPointsModel.DataModel<Any>()
                            data2.id = mb.id
                            data2.code = mb.getCode()
                            data2.dataType = map[mb.getDataType()]
                            data2.value = "6.332"
                            childList2.add(data2)

                        } else if (mb.getDataType() == ModelStyleConstant.TEXT) {
                            val data1 = QuecIotDataPointsModel.DataModel<Any>()
                            data1.id = mb.id
                            data1.code = mb.getCode()
                            data1.dataType = map[mb.getDataType()]
                            data1.value = "text_content1"
                            childList1.add(data1)

                            val data2 = QuecIotDataPointsModel.DataModel<Any>()
                            data2.id = mb.id
                            data2.code = mb.getCode()
                            data2.dataType = map[mb.getDataType()]
                            data2.value = "text_content2"
                            childList2.add(data2)

                        } else if (mb.getDataType() == ModelStyleConstant.DATE) {
                            val data1 = QuecIotDataPointsModel.DataModel<Any>()
                            data1.id = mb.id
                            data1.code = mb.getCode()
                            data1.dataType = map[mb.getDataType()]
                            data1.value = Date().time.toString()
                            childList1.add(data1)

                            val data2 = QuecIotDataPointsModel.DataModel<Any>()
                            data2.id = mb.id
                            data2.code = mb.getCode()
                            data2.dataType = map[mb.getDataType()]
                            data2.value = Date().time.toString()
                            childList2.add(data2)

                        }
                    }

                    val structData1 = QuecIotDataPointsModel.DataModel<Any>()
                    structData1.dataType = map[item.dataType]
                    structData1.value = childList1

                    val structData2 = QuecIotDataPointsModel.DataModel<Any>()
                    structData2.dataType = map[item.dataType]
                    structData2.value = childList2

                    val mListChild: MutableList<QuecIotDataPointsModel.DataModel<Any>> = ArrayList()
                    mListChild.add(structData1)
                    mListChild.add(structData2)

                    val arrayData = QuecIotDataPointsModel.DataModel<Any>();
                    arrayData.id = item.abId
                    arrayData.code = item.resourceCode
                    arrayData.dataType = map[item.dataType]
                    arrayData.value = mListChild
                    deviceControlManager?.writeDps(mutableListOf(arrayData))
                } else {
                    //"[{\"key\":[{\"id\":[{\"key1\":\"value1\"}]},{\"id\":[{\"key2\":\"value2\"}]}]}]"（id: 0）
                    val specs1 = specs.getSpecs()
                    val childArray1 = JSONArray()
                    for (mb in specs1) {
                        if (mb.getDataType() == ModelStyleConstant.BOOL) {
                            val child1 = JSONObject()
                            try {
                                child1.put(mb.getCode(), "true")
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                            childArray1.put(child1)
                        } else if (mb.getDataType() == ModelStyleConstant.ENUM) {
                            val child1 = JSONObject()
                            try {
                                child1.put(mb.getCode(), 2)
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                            childArray1.put(child1)
                        } else if (mb.getDataType() == ModelStyleConstant.INT) {
                            val child1 = JSONObject()
                            try {
                                child1.put(mb.getCode(), 3)
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                            childArray1.put(child1)
                        } else if (mb.getDataType() == ModelStyleConstant.FLOAT) {
                            val child1 = JSONObject()
                            try {
                                child1.put(mb.getCode(), 3.1)
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                            childArray1.put(child1)
                        } else if (mb.getDataType() == ModelStyleConstant.DOUBLE) {
                            val child1 = JSONObject()
                            try {
                                child1.put(mb.getCode(), 5.6)
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                            childArray1.put(child1)
                        } else if (mb.getDataType() == ModelStyleConstant.TEXT) {
                            val child1 = JSONObject()
                            try {
                                child1.put(mb.getCode(), "text")
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                            childArray1.put(child1)
                        } else if (mb.getDataType() == ModelStyleConstant.DATE) {
                            val child1 = JSONObject()
                            try {
                                child1.put(mb.getCode(), Date().time.toString())
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                            childArray1.put(child1)
                        }
                    }
                    val c1 = JSONObject()
                    try {
                        c1.put("0", childArray1)
                        val array = JSONArray()
                        array.put(c1)
                        val obj = JSONObject()
                        obj.put(item.resourceCode, array)
                        val data = JSONArray().put(obj).toString()
                        sendBaseHttpData(data, pk, dk)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        mDialog.show()
    }

    fun sendBaseHttpData(data: String?, pk: String?, dk: String?) {
        val mList: MutableList<BatchControlDevice> = ArrayList()
        val test1 = BatchControlDevice(pk, dk, "", "")
        mList.add(test1)
        //cache time 1 day
        val time = 60 * 60 * 24
        DeviceServiceFactory.getInstance().getService(IDevService::class.java)
            .batchControlDevice(data, mList, time,
                1, 2, 2, 2,
                object : IHttpCallBack {
                    override fun onSuccess(result: String) {
                        println("batchControlDevice--:$result")
                        try {
                            val obj = JSONObject(result)
                            val data = obj.getJSONObject("data")
                            val jarray = data.getJSONArray("failureList")
                            if (jarray != null && jarray.length() > 0) {
                                ToastUtils.showShort(activity, "http delivery failed")
                            } else {
                                ToastUtils.showShort(activity, "http delivery success")
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFail(e: Throwable) {
                        e.printStackTrace()
                    }
                }
            )
    }


    fun sendDps(item: BusinessValue, value: Any) {
        val dp = QuecIotDataPointsModel.DataModel<Any>();
        dp.code = item.resourceCode
        dp.id = item.abId;
        dp.dataType = map[item.dataType.uppercase()]
        dp.value = value
        deviceControlManager?.writeDps(mutableListOf(dp))
        Log.i(
            "sendDps",
            "dp.code=" + dp.code + ",dp.id=" + dp.id + ",dp.dataType=" + dp.dataType + ",dp.value=" + dp.value
        )
    }

    fun setDp(item: BusinessValue, value: Any): QuecIotDataPointsModel.DataModel<Any> {
        val dp = QuecIotDataPointsModel.DataModel<Any>();
        dp.code = item.resourceCode
        dp.id = item.abId;
        dp.dataType = map[item.dataType.uppercase()]
        dp.value = value
        return dp;
    }

    companion object {
        const val DEVICE_ONLINE = 1
    }


    fun covertBussinevalue(mudelBasics: List<ModelBasic<Any>>?): List<BusinessValue> {
        val list: MutableList<BusinessValue> = mutableListOf();
        mudelBasics?.forEach {
            val bsvalue = BusinessValue();
            bsvalue.abId = it.id;
            bsvalue.name = it.name;
            bsvalue.subType = it.subType;
            bsvalue.dataType = it.dataType;
            bsvalue.resourceCode = it.code;
            list.add(bsvalue)
        }
        return list

    }
}

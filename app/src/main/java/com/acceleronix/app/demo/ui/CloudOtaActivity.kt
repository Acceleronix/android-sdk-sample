package com.acceleronix.app.demo.ui

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.reflect.TypeToken
import com.acceleronix.app.demo.R
import com.acceleronix.app.demo.adapter.DeviceOtaAdapter
import com.acceleronix.app.demo.base.BaseActivity
import com.acceleronix.app.demo.bean.DeviceOtaModel
import com.acceleronix.app.demo.bean.DeviceUpgradeSumBean
import com.acceleronix.app.demo.bean.UpgradeDeviceResult
import com.acceleronix.app.demo.utils.MyUtils
import com.acceleronix.app.demo.utils.ToastUtils
import com.acceleronix.app.demo.widget.BottomItemDecorationSystem
import com.quectel.app.quecnetwork.httpservice.IHttpCallBack
import com.quectel.basic.common.entity.QuecResult
import com.quectel.basic.common.utils.QuecGsonUtil
import com.quectel.basic.common.utils.QuecThreadUtil
import com.quectel.basic.queclog.QLog
import com.quectel.sdk.ota.upgrade.model.OtaUpgradeStatus
import com.quectel.sdk.ota.upgrade.model.OtaUpgradeStatusModel
import com.quectel.sdk.ota.upgrade.model.UpgradeDeviceBean
import com.quectel.sdk.ota.upgrade.model.UpgradePlan
import com.quectel.sdk.ota.upgrade.service.IQuecHttpOtaService
import com.quectel.sdk.ota.upgrade.util.QuecHttpOtaServiceFactory
import java.util.Timer
import java.util.TimerTask


class CloudOtaActivity() : BaseActivity() {

    private val TAG = "DeviceOtaActivity"
    private var timer: Timer? = null
    lateinit var deviceOtaAdapter: DeviceOtaAdapter


    override fun getContentLayout(): Int {
        return R.layout.activity_cloud_ota
    }

    override fun addHeadColor() {
        MyUtils.addStatusBarView(this, R.color.gray_bg)
    }


    override fun initData() {
        val intent = intent
        initView()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        timer = null
    }

    private fun initView() {
        deviceOtaAdapter = DeviceOtaAdapter(this, mutableListOf<DeviceOtaModel>())
        val recyclerView = findViewById<RecyclerView>(R.id.rv_cloud_ota)
        recyclerView.setLayoutManager(LinearLayoutManager(this))
        recyclerView.addItemDecoration(BottomItemDecorationSystem(this))
        recyclerView.adapter = deviceOtaAdapter

        findViewById<View>(R.id.iv_back).setOnClickListener {
            finish()
        }
        //Cloud OTA - User has an upgradeable device
        findViewById<View>(R.id.btn_query_cloud_has_device_upgrade).setOnClickListener {
            QuecHttpOtaServiceFactory.getInstance().getService(IQuecHttpOtaService::class.java)
                .getUserIsHaveDeviceUpgrade(null, object :
                    IHttpCallBack {
                    override fun onSuccess(s: String?) {
                        QLog.i(TAG, "getUserIsHaveDeviceUpgrade onSuccess: $s")
                        val result = QuecGsonUtil.getResult(s, DeviceUpgradeSumBean::class.java)
                        if (result.successCode()) {
                            ToastUtils.showShort(
                                this@CloudOtaActivity,
                                "Number of upgradeable devices：${result.data.haveDeviceUpgradeSum}"
                            )
                        } else {
                            ToastUtils.showShort(this@CloudOtaActivity, "Query failed")
                        }

                    }

                    override fun onFail(e: Throwable?) {
                        e?.printStackTrace()
                    }
                })
        }


        //Cloud OTA-Confirm upgrade plan
        findViewById<View>(R.id.btn_commit_cloud_upgrade).setOnClickListener {
            val deviceOtaList = deviceOtaAdapter.data
            if (deviceOtaList == null || deviceOtaList.size == 0) {
                ToastUtils.showShort(this, "Please create an upgrade plan on the platform first")
                return@setOnClickListener
            }

            val planList = ArrayList<UpgradePlan>()
            deviceOtaList.forEach {
                val upgradePlan = UpgradePlan()
                upgradePlan.deviceKey = it.deviceKey
                upgradePlan.productKey = it.productKey
                upgradePlan.planId = it.planId
                upgradePlan.operType = 1 //1-Upgrade immediately (confirm to upgrade at any time) 2-Make an appointment to upgrade (make an appointment to upgrade in a specified time window) 3-(Cancel appointment and cancel upgrade)
                planList.add(upgradePlan)
            }
            QuecHttpOtaServiceFactory.getInstance().getService(IQuecHttpOtaService::class.java)
                .userBatchConfirmUpgradeWithList(planList, object :
                    IHttpCallBack {
                    override fun onSuccess(s: String?) {
                        QLog.i(TAG, "userBatchConfirmUpgradeWithList onSuccess: $s")
                        val result = QuecGsonUtil.getResult(s, UpgradeDeviceResult::class.java)
                        if (result.successCode()) {

                            //Simple processing, start scheduled query once there is a success
                            if (result.data.successList.isNotEmpty()) {
                                //Regularly check the upgrade status
                                startTimer()
                            } else {
                                ToastUtils.showShort(
                                    this@CloudOtaActivity,
                                    "Upgrade confirmation failed"
                                )
                            }

                        } else {
                            ToastUtils.showShort(
                                this@CloudOtaActivity,
                                "Upgrade confirmation failed：${result.msg}"
                            )
                        }
                    }

                    override fun onFail(e: Throwable?) {
                        e?.printStackTrace()
                        ToastUtils.showShort(this@CloudOtaActivity, "Upgrade confirmation failed")
                    }
                })
        }

        //Check Upgrade Plan
        getUpgradePlanDeviceList()

    }

    private fun getUpgradePlanDeviceList() {
        QuecHttpOtaServiceFactory.getInstance().getService(IQuecHttpOtaService::class.java)
            .getUpgradePlanDeviceList(null, 1, 20, object :
                IHttpCallBack {
                override fun onSuccess(s: String?) {
                    QLog.i(TAG, "getDeviceUpgradePlan onSuccess: $s")
                    val result = QuecGsonUtil.getPageResult(s, DeviceOtaModel::class.java)
                    if (result.successCode()) {
                        if (result.data != null && result.data.list != null) {
                            if (result.data.list.isEmpty()) {
                                ToastUtils.showShort(
                                    this@CloudOtaActivity,
                                    "Please create an upgrade plan on the platform first"
                                )
                            }
                            QuecThreadUtil.RunMainThread {
                                deviceOtaAdapter.setList(result.data.list)
                            }

                        }
                    }
                }

                override fun onFail(e: Throwable?) {
                    e?.printStackTrace()
                }
            })
    }

    //Batch query upgrade details
    private fun getBatchUpgradeDetails() {
        val list: MutableList<UpgradeDeviceBean> = ArrayList()
        val deviceOtaList = deviceOtaAdapter.data
        deviceOtaList.forEach {
            val bean = UpgradeDeviceBean()
            bean.deviceKey = it.deviceKey
            bean.planId = it.planId
            bean.productKey = it.productKey
            list.add(bean)
        }

        QuecHttpOtaServiceFactory.getInstance().getService(IQuecHttpOtaService::class.java)
            .getBatchUpgradeDetailsWithList(list, object : IHttpCallBack {
                override fun onSuccess(s: String) {
                    val typeToken = object : TypeToken<QuecResult<List<OtaUpgradeStatusModel>>>() {}
                   val result : QuecResult<List<OtaUpgradeStatusModel>> = QuecGsonUtil.getGson().fromJson(s, typeToken.type)
                    result.data?.forEach { statusModel ->
                        deviceOtaAdapter.data.find { statusModel.deviceKey == it.deviceKey && statusModel.productKey == it.productKey && statusModel.planId == it.planId }
                            ?.apply {
                                upgradeProgress = statusModel.upgradeProgress
                                userConfirmStatus = statusModel.userConfirmStatus
                                deviceStatus = statusModel.deviceStatus
                            }
                    }
                    QuecThreadUtil.RunMainThread {
                        deviceOtaAdapter.notifyDataSetChanged()
                    }
                    //Determine whether all upgrades have been completed. If so, cancel the timer.
                    if (isAllPlansFinished()) {
                        timer?.cancel()
                        timer = null
                    }
                }

                override fun onFail(e: Throwable) {
                    e.printStackTrace()
                }
            })
    }

    //Timer, check the upgrade details every 3 seconds
    private fun startTimer() {
        if (timer != null) return
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                getBatchUpgradeDetails()
            }
        }, 0, 3000)
    }

    private fun isAllPlansFinished(): Boolean {
        deviceOtaAdapter.data.forEach {
            if (it.deviceStatus == OtaUpgradeStatus.UPGRADING
                || (it.userConfirmStatus == OtaUpgradeStatus.UPGRADING && it.deviceStatus == OtaUpgradeStatus.NOT_UPGRADE)
            ) {
                return false
            }
        }
        return true
    }

}

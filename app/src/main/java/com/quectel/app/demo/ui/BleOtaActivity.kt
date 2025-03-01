package com.quectel.app.demo.ui

import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.ScrollView
import android.widget.TextView
import com.quectel.app.blesdk.v2.api.BleFileErrorType
import com.quectel.app.demo.R
import com.quectel.app.demo.base.BaseActivity
import com.quectel.app.demo.utils.MyUtils
import com.quectel.basic.common.utils.QuecToastUtil
import com.quectel.basic.queclog.QLog
import com.quectel.sdk.ota.upgrade.manager.QuecBleOtaManager
import com.quectel.sdk.ota.upgrade.manager.QuecBleOtaManager.ProgressListener
import com.quectel.sdk.ota.upgrade.manager.QuecBleOtaManager.StateListener
import com.quectel.sdk.ota.upgrade.model.QuecBleOtaInfo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class BleOtaActivity() : BaseActivity() {
    private lateinit var tvInfo: TextView
    private val optionList = ArrayList<Item>()
    private val infoBuild = StringBuilder()
    private val dataFormat = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
    private lateinit var pk: String
    private lateinit var dk: String

    private var otaInfo: QuecBleOtaInfo? = null
    private val stateListener = object : StateListener {
        override fun onFail(pk: String, dk: String, errorCode: BleFileErrorType) {
            showInfo("onFail $errorCode")
        }

        override fun onSuccess(pk: String, dk: String, waitTime: Long) {
            showInfo("onSuccess $waitTime ms")
        }
    }

    private val progressListener =
        ProgressListener { pk, dk, progress -> showInfo("onUpdate $progress") }

    override fun getContentLayout(): Int {
        return R.layout.activity_ble_ota
    }

    override fun addHeadColor() {
        MyUtils.addStatusBarView(this, R.color.gray_bg)
    }

    override fun onResume() {
        super.onResume()

        QuecBleOtaManager.addStateListener(stateListener)
        QuecBleOtaManager.addProgressListener(progressListener)
    }

    override fun onPause() {
        super.onPause()

        QuecBleOtaManager.removeStateListener(stateListener)
        QuecBleOtaManager.removeProgressListener(progressListener)
    }

    override fun initData() {
        val intent = intent
        pk = intent.getStringExtra(KEY_PK) ?: ""
        dk = intent.getStringExtra(KEY_DK) ?: ""

        addItem("Check Upgrade Plan") {
            QuecBleOtaManager.checkVersion(pk, dk) {
                if (it.isSuccess) {
                    otaInfo = it.data
                }
                showInfo("Query the upgrade plan results: $it")
            }
        }

        addItem("Start Upgrading") {
            val info = otaInfo
            if (info != null) {
                QuecBleOtaManager.startOta(listOf(info))
            } else {
                showInfo("Please check the upgrade plan first")
            }
        }

        addItem("Stop Upgrading") {
            val info = otaInfo
            if (info != null) {
                QuecBleOtaManager.stopOta(listOf(info))
            } else {
                showInfo("Please check the upgrade plan first")
            }
        }

        tvInfo = findViewById(R.id.tv_info)

        findViewById<ListView>(R.id.lv_list).let {
            it.adapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                optionList.map { item -> item.title })
            it.setOnItemClickListener { _, _, position, _ ->
                optionList[position].onClick()
            }
        }

        findViewById<TextView>(R.id.tv_clear).setOnClickListener {
            infoBuild.clear()
            tvInfo.text = ""
            QuecToastUtil.showS("Operation log has been cleared")
        }

        findViewById<ImageView>(R.id.iv_back).setOnClickListener { finish() }
    }

    private fun showInfo(info: String) {
        QLog.i(TAG, info)
        infoBuild
            .append(dataFormat.format(Date()))
            .append(" ")
            .append(info)
            .append("\n")
        tvInfo.text = infoBuild.toString()
        tvInfo.post {
            findViewById<ScrollView>(R.id.sv_log).smoothScrollTo(0, tvInfo.height)
        }
    }

    private fun addItem(title: String, onClick: () -> Unit) {
        showInfo(title)
        optionList.add(Item(title, onClick))
    }

    private data class Item(
        val title: String,
        val onClick: () -> Unit,
    )

    companion object {
        private const val TAG = "BleOtaActivity"
        const val KEY_PK = "pk"
        const val KEY_DK = "dk"
    }
}

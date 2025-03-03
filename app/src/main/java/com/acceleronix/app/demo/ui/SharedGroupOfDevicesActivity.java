package com.acceleronix.app.demo.ui;

import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.acceleronix.app.demo.R;
import com.acceleronix.app.demo.adapter.SharedDeviceAdapter;
import com.acceleronix.app.demo.base.BaseActivity;
import com.acceleronix.app.demo.bean.SharedDevice;
import com.acceleronix.app.demo.utils.MyUtils;
import com.acceleronix.app.demo.utils.ToastUtils;
import com.acceleronix.app.demo.widget.BottomItemDecorationSystem;
import com.quectel.app.device.deviceservice.IDevService;
import com.quectel.app.device.utils.DeviceServiceFactory;
import com.quectel.app.quecnetwork.httpservice.IHttpCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SharedGroupOfDevicesActivity extends BaseActivity {

    @Override
    protected int getContentLayout() {
        return R.layout.activity_shared_group_device;
    }

    @Override
    protected void addHeadColor() {
        MyUtils.addStatusBarView(this, R.color.gray_bg);
    }

    String shareCode = null;
    String content = null;

    @BindView(R.id.mList)
    RecyclerView mRecyclerView;
    SharedDeviceAdapter mAdapter;

    List<SharedDevice> mList = null;
    @Override
    protected void initData() {

        shareCode =  getIntent().getStringExtra("shareCode");
        content = getIntent().getStringExtra("content");
        System.out.println("content--:"+content);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        mRecyclerView.addItemDecoration(new BottomItemDecorationSystem(activity));

        try {
            JSONObject mainObj = new JSONObject(content);
            int code =  mainObj.getInt("code");
            if(code==200)
            {
                JSONObject obj = mainObj.getJSONObject("data");
                JSONArray array =  obj.getJSONArray("list");
                Type type =new TypeToken<List<SharedDevice>>() {}.getType();
                mList = new Gson().fromJson(array.toString(), type);

                if(mList.size()<=0)
                {
                    ToastUtils.showLong(activity,"There are no devices in the group");
                    return;
                }

                mAdapter = new SharedDeviceAdapter(activity, mList);
                mRecyclerView.setAdapter(mAdapter);

                mAdapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                        System.out.println("position--:"+position);
                        SharedDevice item = mList.get(position);
                        createChangeNameDialog(shareCode,item);

                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void createChangeNameDialog(String shareCode,SharedDevice item)
    {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.accepter_change_device_ingroup_dialog, null);
        final Dialog mDialog = new Dialog(activity, R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        EditText edit_name = (EditText) mDialog.findViewById(R.id.edit_name);

        Button bt_sure = (Button) mDialog.findViewById(R.id.bt_sure);
        Button bt_cancel = (Button) mDialog.findViewById(R.id.bt_cancel);
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
        bt_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name =  MyUtils.getEditTextContent(edit_name);

                if(TextUtils.isEmpty(name))
                {
                    ToastUtils.showShort(activity,"Input cannot be empty");
                    return;
                }
                mDialog.dismiss();
                startLoading();
                DeviceServiceFactory.getInstance().getService(IDevService.class).shareUserSetDeviceName(name,item.getPk(),item.getDk(),shareCode,
                        new IHttpCallBack() {
                            @Override
                            public void onSuccess(String result) {
                                finishLoading();
                                try {
                                    JSONObject  obj = new JSONObject(result);
                                    if (obj.getInt("code") == 200) {
                                       ToastUtils.showShort(activity,"Modification successful");
                                       finish();
                                    }
                                    else
                                    {
                                        ToastUtils.showShort(activity,obj.getString("msg"));
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                            @Override
                            public void onFail(Throwable e) {
                                e.printStackTrace();
                            }
                        }
                );

            }
        });

        mDialog.show();
    }


    @OnClick({R.id.iv_back})
    public void buttonClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }

    }


}

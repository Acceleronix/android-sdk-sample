package com.acceleronix.app.demo.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.google.gson.Gson;
import com.acceleronix.app.demo.R;
import com.acceleronix.app.demo.adapter.DeviceAdapter;
import com.acceleronix.app.demo.bean.UserDeviceList;
import com.acceleronix.app.demo.fragmentbase.BaseMainFragment;
import com.acceleronix.app.demo.ui.BleOtaActivity;
import com.acceleronix.app.demo.ui.DeviceControlActivity;
import com.acceleronix.app.demo.ui.DistributionNetworkActivity;
import com.acceleronix.app.demo.ui.SelectOtaActivity;
import com.acceleronix.app.demo.utils.MyUtils;
import com.acceleronix.app.demo.utils.ToastUtils;
import com.acceleronix.app.demo.widget.BottomItemDecorationSystem;
import com.acceleronix.app.demo.widget.PayBottomDialog;
import com.quectel.app.device.deviceservice.IDevService;
import com.quectel.app.device.iot.IotChannelController;
import com.quectel.app.device.utils.DeviceServiceFactory;
import com.quectel.app.quecnetwork.httpservice.IHttpCallBack;
import com.quectel.basic.common.entity.QuecDeviceModel;
import com.quectel.basic.common.interfaces.QuecClickListener;
import com.quectel.basic.common.utils.QuecThreadUtil;
import com.quectel.sdk.iot.channel.kit.chanel.IQuecChannelManager;
import com.quectel.sdk.iot.channel.kit.constaint.QuecIotChannelType;
import com.quectel.sdk.iot.channel.kit.model.QuecIotDataPointsModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;


public class DeviceFragment extends BaseMainFragment {

    //  EditText edit_device_name;
    public static synchronized DeviceFragment newInstance() {
        DeviceFragment frag = new DeviceFragment();

        return frag;
    }

    @Override
    protected void getNeedArguments(Bundle bundle) {


    }

    @Override
    protected int getLayoutView() {
        return R.layout.device_layout;
    }

    @Override
    protected void processBusiness() {


    }

    @BindView(R.id.mList)
    RecyclerView mRecyclerView;
    DeviceAdapter mAdapter;

    @BindView(R.id.fragment_ptr_home_ptr_frame)
    PtrFrameLayout mPtrFrameLayout;

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new BottomItemDecorationSystem(getActivity()));
        queryDevice();

        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {

                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mRecyclerView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                refreshData();
            }
        });
        mPtrFrameLayout.disableWhenHorizontalMove(true);
        initConnectListener();
    }

    private void initConnectListener(){
        IotChannelController.getInstance().init();
        IotChannelController.getInstance().setListener(new IQuecChannelManager.IQuecCallBackListener() {
            @Override
            public void onConnect(String channelId, QuecIotChannelType channelType, boolean isSuccess, String errMsg) {
                QuecThreadUtil.RunMainThread(() -> {
                    updateConnectStatus( channelId, channelType, isSuccess);
                });

            }

            @Override
            public void onData(String channelId, QuecIotChannelType channelType, QuecIotDataPointsModel dataPointsModel) {

            }

            @Override
            public void onBleClose(String channelId) {

            }

            @Override
            public void onDisConnect(String channelId, QuecIotChannelType channelType) {
                QuecThreadUtil.RunMainThread(() -> {
                    updateConnectStatus( channelId, channelType, false);
                });
            }
        });
    }

    private void updateConnectStatus(String channelId, QuecIotChannelType channelType, boolean isConnected) {
        String[] strArray = channelId.split("_");
        String productKey = strArray[0];
        String deviceKey = strArray[1];
        List<UserDeviceList.DataBean.ListBean> list = mAdapter.getData();
        for (UserDeviceList.DataBean.ListBean bean : list) {
            if (bean.getProductKey().equals(productKey) && bean.getDeviceKey().equals(deviceKey)) {
                int onlineStatus = IotChannelController.getInstance().getOnlineState(channelId);
                // 0:offline other:online 1: websocket online, 2 : wifi online, 3: wifi + websocket online, 4: ble online, 5: websocket + ble online, 6: ble + wifi online , 7: wifi + ble + ws online
                bean.setOnlineStatus(onlineStatus);
                bean.setDeviceStatus( onlineStatus != 0 ? "Online" : "Offline");
                mAdapter.notifyDataSetChanged();
                return;
            }
        }
    }

    private void queryDevice() {
        startLoading();
        DeviceServiceFactory.getInstance().getService(IDevService.class).queryUserDeviceList("", 1, 50,
                new IHttpCallBack() {
                    @Override
                    public void onSuccess(String result) {
                        mPtrFrameLayout.refreshComplete();
                        finishLoading();
                        UserDeviceList userDeviceList = new Gson().fromJson(result, UserDeviceList.class);
                        if(userDeviceList==null|| userDeviceList.getData()==null )return;
                        List<UserDeviceList.DataBean.ListBean> mList = userDeviceList.getData().getList();
                        System.out.println("mList--:" + mList.size());

                        if (mList != null && mList.size() > 0) {
                            if (mAdapter == null) {
                                mAdapter = new DeviceAdapter(getActivity(), mList);
                                mAdapter.setAnimationEnable(true);
                                mRecyclerView.setAdapter(mAdapter);

                                List<QuecDeviceModel> quecDeviceModels = new ArrayList<>();
                                for (UserDeviceList.DataBean.ListBean bean : mList) {
                                    QuecDeviceModel quecDeviceModel = new QuecDeviceModel(bean.getProductKey(), bean.getDeviceKey());
                                    quecDeviceModel.setCapabilitiesBitmask(bean.getCapabilitiesBitmask());
                                    if (bean.getCapabilitiesBitmask() == 4) {
                                        String bindingCode = bean.getBindingCode();
                                        if (!TextUtils.isEmpty(bindingCode)) {
                                            String bindingKey = Base64.encodeToString(bindingCode.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
                                            quecDeviceModel.setBindingkey(bindingKey);
                                        }
                                    }else {
                                        quecDeviceModel.setBindingkey(bean.getAuthKey());
                                    }

                                    quecDeviceModels.add(quecDeviceModel);
                                }
                                IotChannelController.getInstance().startChannels(requireContext(), quecDeviceModels, null);

                            } else {
                                List<UserDeviceList.DataBean.ListBean> list = mAdapter.getData();
                                list.clear();
                                list.addAll(mList);
                                mAdapter.notifyDataSetChanged();
                            }

                            mAdapter.setOnItemClickListener(new OnItemClickListener() {
                                @Override
                                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                                    UserDeviceList.DataBean.ListBean lanVO = mAdapter.getData().get(position);
                                    createSelectDialog(lanVO,lanVO.getProductKey(), lanVO.getDeviceKey(), lanVO.getShareCode(), lanVO.getDeviceStatus(), lanVO.getCapabilitiesBitmask());
                                }
                            });


                        }

                    }

                    @Override
                    public void onFail(Throwable e) {
                        mPtrFrameLayout.refreshComplete();
                        e.printStackTrace();
                    }
                }
        );
    }

    private static final int PAGE_SIZE = 10;
    int page = 0;

    private void refreshData() {
        queryDevice();

    }


    @OnClick({R.id.iv_add, R.id.tv_ota})
    public void buttonClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.tv_ota:
                intent = new Intent(getActivity(), SelectOtaActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_add:
                System.out.println("iv_add");
                View dialogView = getLayoutInflater().inflate(R.layout.bottom_pop_device_layout, null);
                PayBottomDialog myDialog = new PayBottomDialog(getActivity(), dialogView, new int[]{R.id.bt_cancel,
                        R.id.bt_sn_bind, R.id.bt_unbind, R.id.bt_receive_share, R.id.bt_wifi});
                myDialog.bottmShow();
                myDialog.setOnBottomItemClickListener(new PayBottomDialog.OnBottomItemClickListener() {
                    @Override
                    public void onBottomItemClick(PayBottomDialog dialog, View view) {
                        Intent intent = null;

                        switch (view.getId()) {
                            case R.id.bt_cancel:
                                myDialog.cancel();
                                break;

                            case R.id.bt_sn_bind:
                                myDialog.cancel();
                                createBindDialog();
                                break;

                            case R.id.bt_unbind:
                                myDialog.cancel();
                                createUnBindDialog();
                                break;

                            case R.id.bt_receive_share:
                                myDialog.cancel();
                                createReceiveShare();
                                break;

                            case R.id.bt_wifi:
                                myDialog.cancel();
                                intent = new Intent(getActivity(), DistributionNetworkActivity.class);
                                startActivity(intent);

                                break;
                        }
                    }
                });

                break;
        }

    }

    private void createReceiveShare() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.receiver_accept_shareinformation_dialog, null);
        final Dialog mDialog = new Dialog(getActivity(), R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        EditText edit_share_code = (EditText) mDialog.findViewById(R.id.edit_share_code);
        Button bt_cancel = (Button) mDialog.findViewById(R.id.bt_cancel);
        Button bt_sure = (Button) mDialog.findViewById(R.id.bt_sure);
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();

            }
        });

        bt_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                String code = MyUtils.getEditTextContent(edit_share_code);
                if (TextUtils.isEmpty(code)) {
                    return;
                }
                startLoading();
                DeviceServiceFactory.getInstance().getService(IDevService.class).acceptShareDevice("", code,
                        new IHttpCallBack() {
                            @Override
                            public void onSuccess(String result) {
                                finishLoading();
                                try {
                                    JSONObject obj = new JSONObject(result);
                                    if (obj.getInt("code") == 200) {
                                        ToastUtils.showShort(getActivity(), "Operation successful");
                                        queryDevice();
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

    private void receiverCancelShare() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.receiver_cancel_share_dialog, null);
        final Dialog mDialog = new Dialog(getActivity(), R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        EditText edit_code = (EditText) mDialog.findViewById(R.id.edit_code);
        Button bt_cancel = (Button) mDialog.findViewById(R.id.bt_cancel);
        Button bt_sure = (Button) mDialog.findViewById(R.id.bt_sure);
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });

        bt_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                String code = MyUtils.getEditTextContent(edit_code);
                if (TextUtils.isEmpty(code)) {
                    return;
                }
                startLoading();
                DeviceServiceFactory.getInstance().getService(IDevService.class).cancelShareByReceiver(code,
                        new IHttpCallBack() {
                            @Override
                            public void onSuccess(String result) {
                                finishLoading();
                                try {
                                    JSONObject obj = new JSONObject(result);
                                    if (obj.getInt("code") == 200) {
                                        ToastUtils.showShort(getActivity(), "Operation successful");
                                        queryDevice();
                                    } else {
                                        ToastUtils.showShort(getActivity(), obj.getString("msg"));
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


    private void ownerCancelShare() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.owner_cancel_share_dialog, null);
        final Dialog mDialog = new Dialog(getActivity(), R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        EditText edit_code = (EditText) mDialog.findViewById(R.id.edit_code);
        Button bt_cancel = (Button) mDialog.findViewById(R.id.bt_cancel);
        Button bt_sure = (Button) mDialog.findViewById(R.id.bt_sure);
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });

        bt_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                String code = MyUtils.getEditTextContent(edit_code);
                if (TextUtils.isEmpty(code)) {
                    return;
                }
                startLoading();
                DeviceServiceFactory.getInstance().getService(IDevService.class).cancelShareByOwner(code,
                        new IHttpCallBack() {
                            @Override
                            public void onSuccess(String result) {
                                finishLoading();
                                try {
                                    JSONObject obj = new JSONObject(result);
                                    if (obj.getInt("code") == 200) {
                                        ToastUtils.showShort(getActivity(), "Operation successful");
                                    } else {
                                        ToastUtils.showShort(getActivity(), obj.getString("msg"));
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

    private void createBindDialog() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.bind_device_dialog, null);
        final Dialog mDialog = new Dialog(getActivity(), R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        EditText edit_pk = (EditText) mDialog.findViewById(R.id.edit_pk);
        EditText edit_sn = (EditText) mDialog.findViewById(R.id.edit_sn);
        EditText edit_device_name = (EditText) mDialog.findViewById(R.id.edit_device_name);
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
                String pk = MyUtils.getEditTextContent(edit_pk);
                String sn = MyUtils.getEditTextContent(edit_sn);
                if (TextUtils.isEmpty(pk)) {
                    ToastUtils.showShort(getActivity(), "The value of pk cannot be empty.");
                    return;
                }
                if (TextUtils.isEmpty(sn)) {
                    ToastUtils.showShort(getActivity(), "The value of sn cannot be empty.");
                    return;
                }
                String deviceName = "";
                String name = MyUtils.getEditTextContent(edit_device_name);
                if (!TextUtils.isEmpty(name)) {
                    deviceName = name;
                }

                mDialog.dismiss();
                DeviceServiceFactory.getInstance().getService(IDevService.class).bindDeviceSn(pk, sn, deviceName,
                        new IHttpCallBack() {
                            @Override
                            public void onSuccess(String result) {
                                //{"code":200,"msg":"","data":{"pk":"p111ti","dk":"888866669999001","moduleType":null}}
                                try {
                                    JSONObject obj = new JSONObject(result);
                                    if (obj.getInt("code") == 200) {
                                        queryDevice();
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

    private void createSelectDialog(UserDeviceList.DataBean.ListBean lanVO,String pk, String dk, String shareCode, String deviceStatus, int bitmask) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.device_select_dialog, null);
        final Dialog mDialog = new Dialog(getActivity(), R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        Button bt_changeDevice = (Button) mDialog.findViewById(R.id.bt_changeDevice);
        Button bt_queryDeviceInfor = (Button) mDialog.findViewById(R.id.bt_queryDeviceInfor);
        Button bt_set_share = (Button) mDialog.findViewById(R.id.bt_set_share);
        Button bt_queryShareUserList = (Button) mDialog.findViewById(R.id.bt_queryShareUserList);
        Button bt_queryUpgradePlan = (Button) mDialog.findViewById(R.id.bt_queryUpgradePlan);
        Button bt_owner_cancelShare = (Button) mDialog.findViewById(R.id.bt_owner_cancelShare);
        Button bt_cancelShareByReceiver = (Button) mDialog.findViewById(R.id.bt_cancelShareByReceiver);
        Button bt_device_control = (Button) mDialog.findViewById(R.id.bt_device_control);
        Button bt_query_group = (Button) mDialog.findViewById(R.id.bt_query_group);
        Button bt_ble_ota = mDialog.findViewById(R.id.bt_ble_ota);

        bt_query_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                startLoading();
                DeviceServiceFactory.getInstance().getService(IDevService.class).queryGroupListByDevice(pk, dk, new IHttpCallBack() {
                    @Override
                    public void onSuccess(String result) {
                        finishLoading();
                        ToastUtils.showShort(getActivity(), result);
                    }

                    @Override
                    public void onFail(Throwable e) {

                    }
                });
            }
        });

        bt_device_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                Intent intent = new Intent(getActivity(), DeviceControlActivity.class);
                intent.putExtra("device", (Serializable) lanVO);
                startActivity(intent);
            }
        });

        Button bt_cancel = (Button) mDialog.findViewById(R.id.bt_cancel);
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });

        bt_cancelShareByReceiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                receiverCancelShare();
            }
        });

        bt_owner_cancelShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                ownerCancelShare();
            }
        });

        bt_queryUpgradePlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                startLoading();
                DeviceServiceFactory.getInstance().getService(IDevService.class).queryFetchPlan(pk, dk, new IHttpCallBack() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject obj = new JSONObject(result);
                            if (obj.getInt("code") == 200) {
                                JSONObject obj2 = obj.getJSONObject("data");
                                JSONObject obj3 = (JSONObject) obj2.getJSONArray("components").get(0);
                                String componentNo = obj3.getString("componentNo");
                                DeviceServiceFactory.getInstance().getService(IDevService.class).reportUpgradeStatus(pk, dk, componentNo, 10,
                                        new IHttpCallBack() {
                                            @Override
                                            public void onSuccess(String result) {
                                                finishLoading();
                                                ToastUtils.showShort(getActivity(), result);
                                            }

                                            @Override
                                            public void onFail(Throwable e) {
                                                e.printStackTrace();
                                            }
                                        }
                                );
                            } else {
                                finishLoading();
                                ToastUtils.showShort(getActivity(), obj.getString("msg"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail(Throwable e) {
                    }
                });

            }
        });

        bt_queryShareUserList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                startLoading();
                DeviceServiceFactory.getInstance().getService(IDevService.class).getDeviceShareUserList(pk, dk, new IHttpCallBack() {
                    @Override
                    public void onSuccess(String result) {
                        // System.out.println("result-getDeviceShareUserList-:"+result);
                        finishLoading();
                        ToastUtils.showShort(getActivity(), result);
                    }

                    @Override
                    public void onFail(Throwable e) {


                    }
                });

            }
        });

        bt_changeDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                updateDeviceNameDialog(pk, dk, shareCode);
            }
        });

        bt_set_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                generateShareInfor(pk, dk);

            }
        });

        bt_queryDeviceInfor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                startLoading();
                if (TextUtils.isEmpty(shareCode)) {
                    DeviceServiceFactory.getInstance().getService(IDevService.class).queryDeviceInfo(pk, dk, "", new IHttpCallBack() {
                        @Override
                        public void onSuccess(String result) {
                            finishLoading();
                            ToastUtils.showShort(getActivity(), result);
                        }

                        @Override
                        public void onFail(Throwable e) {

                        }
                    });
                } else {
                    DeviceServiceFactory.getInstance().getService(IDevService.class).queryDeviceInfo(pk, "", shareCode, new IHttpCallBack() {
                        @Override
                        public void onSuccess(String result) {
                            finishLoading();
                            ToastUtils.showShort(getActivity(), result);
                        }

                        @Override
                        public void onFail(Throwable e) {


                        }
                    });
                }
            }
        });

        if (bitmask == 4) {
            bt_ble_ota.setVisibility(View.VISIBLE);
            bt_ble_ota.setOnClickListener(new QuecClickListener() {
                @Override
                public void onViewClick(View v) {
                    Intent intent = new Intent(getActivity(), BleOtaActivity.class);
                    intent.putExtra(BleOtaActivity.KEY_PK, pk);
                    intent.putExtra(BleOtaActivity.KEY_DK, dk);
                    startActivity(intent);
                }
            });
        }

        mDialog.show();
    }

    private void generateShareInfor(String pk, String dk) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.sharer_generate_information_dialog, null);
        final Dialog mDialog = new Dialog(getActivity(), R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        Button bt_generate = (Button) mDialog.findViewById(R.id.bt_generate);
        TextView tv_share_infor = (TextView) mDialog.findViewById(R.id.tv_share_infor);
        Button bt_copy = (Button) mDialog.findViewById(R.id.bt_copy);
        Button bt_cancel = (Button) mDialog.findViewById(R.id.bt_cancel);

        bt_generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int time = 1 * 24 * 60 * 60 * 1000;
                long useTime = new Date().getTime();
                useTime = useTime + time;
                startLoading();
                DeviceServiceFactory.getInstance().getService(IDevService.class).shareDeviceInfo(useTime, pk, dk, 0, 0,
                        new IHttpCallBack() {
                            @Override
                            public void onSuccess(String result) {
                                finishLoading();
                                //{"code":200,"msg":"","data":"share_dv_C18222055e793c4ee4581b18d3bfcd1a50ada"}
                                System.out.println("shareDeviceInfo--:" + result);
                                try {
                                    JSONObject obj = new JSONObject(result);
                                    if (obj.getInt("code") == 200) {
                                        String infor = obj.getString("data");
                                        tv_share_infor.setText(infor);
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

        bt_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.copyContentToClipboard(getActivity(), tv_share_infor.getText().toString().trim());
                ToastUtils.showShort(getActivity(), "Copy Success");
            }
        });

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });

        mDialog.show();

    }

    private void createUnBindDialog() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.unbind_device_dialog, null);
        final Dialog mDialog = new Dialog(getActivity(), R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        EditText edit_pk = (EditText) mDialog.findViewById(R.id.edit_pk);
        EditText edit_dk = (EditText) mDialog.findViewById(R.id.edit_dk);
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
                String pk = MyUtils.getEditTextContent(edit_pk);
                String dk = MyUtils.getEditTextContent(edit_dk);
                if (TextUtils.isEmpty(pk)) {
                    ToastUtils.showShort(getActivity(), "The value of pk cannot be empty.");
                    return;
                }
                if (TextUtils.isEmpty(dk)) {
                    ToastUtils.showShort(getActivity(), "The value of dk cannot be empty.");
                    return;
                }

                mDialog.dismiss();
                DeviceServiceFactory.getInstance().getService(IDevService.class).unBindDevice(pk, dk,
                        new IHttpCallBack() {
                            @Override
                            public void onSuccess(String result) {
                                try {
                                    JSONObject obj = new JSONObject(result);
                                    if (obj.getInt("code") == 200) {
                                        queryDevice();
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


    private void updateDeviceNameDialog(String pk, String dk, String shareCode) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.update_device_name_dialog, null);
        final Dialog mDialog = new Dialog(getActivity(), R.style.dialogTM);
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
                String deviceName = MyUtils.getEditTextContent(edit_name);
                if (TextUtils.isEmpty(deviceName)) {
                    ToastUtils.showShort(getActivity(), "The value of device name cannot be empty.");
                    return;
                }

                mDialog.dismiss();
                startLoading();

                if (TextUtils.isEmpty(shareCode)) {
                    DeviceServiceFactory.getInstance().getService(IDevService.class).changeDeviceInfo(deviceName, pk, dk,
                            new IHttpCallBack() {
                                @Override
                                public void onSuccess(String result) {
                                    try {
                                        JSONObject obj = new JSONObject(result);
                                        if (obj.getInt("code") == 200) {
                                            queryDevice();
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
                } else {
                    DeviceServiceFactory.getInstance().getService(IDevService.class).changeShareDeviceName(deviceName, shareCode,
                            new IHttpCallBack() {
                                @Override
                                public void onSuccess(String result) {
                                    try {
                                        JSONObject obj = new JSONObject(result);
                                        if (obj.getInt("code") == 200) {
                                            queryDevice();
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

            }
        });

        mDialog.show();
    }

    String pk = "p111ti";
    String sn = "869537055280516";
    String dk = "869537055280516";

    private void shareDevice(long time) {
        DeviceServiceFactory.getInstance().getService(IDevService.class).shareDeviceInfo(time, pk, dk, 0, 0,
                new IHttpCallBack() {
                    @Override
                    public void onSuccess(String result) {
                        //{"code":200,"msg":"","data":"share_dv_C18222055e793c4ee4581b18d3bfcd1a50ada"}
                        System.out.println("shareDeviceInfo--:" + result);
                    }

                    @Override
                    public void onFail(Throwable e) {
                        e.printStackTrace();
                    }
                }
        );
    }

}

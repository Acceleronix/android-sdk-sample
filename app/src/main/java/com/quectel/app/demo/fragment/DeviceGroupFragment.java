package com.quectel.app.demo.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.gson.reflect.TypeToken;
import com.quectel.app.demo.R;
import com.quectel.app.demo.adapter.DeviceGroupAdapter;
import com.quectel.app.demo.bean.DeviceGroupVO;
import com.quectel.app.demo.fragmentbase.BaseMainFragment;
import com.quectel.app.demo.ui.SharedGroupOfDevicesActivity;
import com.quectel.app.demo.utils.MyUtils;
import com.quectel.app.demo.utils.ToastUtils;
import com.quectel.app.demo.widget.BottomItemDecorationSystem;
import com.quectel.app.demo.widget.PayBottomDialog;
import com.quectel.app.device.bean.UpdateGroup;
import com.quectel.app.device.deviceservice.IDevService;
import com.quectel.app.device.param.AddDeviceParam;
import com.quectel.app.device.utils.DeviceServiceFactory;
import com.quectel.app.quecnetwork.httpservice.IHttpCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;


public class DeviceGroupFragment extends BaseMainFragment {


    public static synchronized DeviceGroupFragment newInstance() {
        DeviceGroupFragment frag = new DeviceGroupFragment();

        return frag;
    }

    @Override
    protected void getNeedArguments(Bundle bundle) {

    }

    @Override
    protected int getLayoutView() {

        return R.layout.device_group_layout;

    }

    @Override
    protected void processBusiness() {


    }

    @BindView(R.id.mList)
    RecyclerView mRecyclerView;
    DeviceGroupAdapter mAdapter;

    @BindView(R.id.fragment_ptr_home_ptr_frame)
    PtrFrameLayout mPtrFrameLayout;
    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new BottomItemDecorationSystem(getActivity()));
        queryGroupList();

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

    }
    List<DeviceGroupVO> mList = null;
    private void queryGroupList()
    {
        startLoading();
        DeviceServiceFactory.getInstance().getService(IDevService.class).queryDeviceGroupList(1,10,
                new IHttpCallBack() {
                    @Override
                    public void onSuccess(String result) {
                          finishLoading();
                        mPtrFrameLayout.refreshComplete();
                        System.out.println("queryGroupList-frag-:"+result);
                        try {
                            JSONObject mainObj = new JSONObject(result);
                            int code =  mainObj.getInt("code");
                            if(code==200)
                            {
                                JSONObject obj = mainObj.getJSONObject("data");
                                JSONArray array =  obj.getJSONArray("list");
                                Type type =new TypeToken<List<DeviceGroupVO>>() {}.getType();
                                 mList = new Gson().fromJson(array.toString(), type);
                                 System.out.println("mList--:"+mList.size());

                                mAdapter = new DeviceGroupAdapter(getActivity(), mList);
                                mRecyclerView.setAdapter(mAdapter);

                                mAdapter.setOnItemClickListener(new OnItemClickListener() {
                                    @Override
                                    public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                                        System.out.println("position--:"+position);
                                        DeviceGroupVO deviceGroupVO = mList.get(position);
                                        createSelectDialog(deviceGroupVO);

                                    }
                                });

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
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
    int  page = 0;
    private void refreshData() {

        queryGroupList();
    }

//    @Override
//    public boolean onBackPressedSupport() {
//        return false;
//    }


    @OnClick({R.id.iv_add})
    public void onViewClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.iv_add:
                View dialogView = getLayoutInflater().inflate(R.layout.bottom_pop_devicegroup_layout, null);
                PayBottomDialog myDialog = new PayBottomDialog(getActivity(), dialogView, new int[]{R.id.bt_cancel,
                        R.id.bt_add_group,R.id.bt_receive_group_share});
                myDialog.bottmShow();
                myDialog.setOnBottomItemClickListener(new PayBottomDialog.OnBottomItemClickListener() {
                    @Override
                    public void onBottomItemClick(PayBottomDialog dialog, View view) {
                        Intent intent = null;

                        switch (view.getId()) {
                            case R.id.bt_cancel:
                                myDialog.cancel();
                                break;

                            case R.id.bt_add_group:
                                myDialog.cancel();
                                createAddGroupDialog();
                                break;

                            case R.id.bt_receive_group_share:
                                myDialog.cancel();
                                createReceiveGroupShare();

                                break;

                        }
                    }
                });


                break;
        }
    }

    private void createAddGroupDialog()
    {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.add_group, null);
        final Dialog mDialog = new Dialog(getActivity(), R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        EditText edit_name = (EditText) mDialog.findViewById(R.id.edit_name);
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
                String name = MyUtils.getEditTextContent(edit_name);
                if(TextUtils.isEmpty(name))
                {
                    return;
                }
                startLoading();
                DeviceServiceFactory.getInstance().getService(IDevService.class).addDeviceGroup(name,
                        new IHttpCallBack() {
                            @Override
                            public void onSuccess(String result) {
                                finishLoading();
                                try {
                                    JSONObject obj = new JSONObject(result);
                                    if (obj.getInt("code") == 200) {
                                        queryGroupList();
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

    private void createSelectDialog(DeviceGroupVO item) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.devicegroup_select_dialog, null);
        final Dialog mDialog = new Dialog(getActivity(), R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        Button bt_queryGroup = (Button) mDialog.findViewById(R.id.bt_queryGroup);
        Button bt_change_device_group = (Button) mDialog.findViewById(R.id.bt_change_device_group);
        Button bt_add_device_to_group = (Button) mDialog.findViewById(R.id.bt_add_device_to_group);
        Button bt_query_in_group = (Button) mDialog.findViewById(R.id.bt_query_in_group);
        Button bt_delete_device_in_group = (Button) mDialog.findViewById(R.id.bt_delete_device_in_group);
        Button bt_delete_group = (Button) mDialog.findViewById(R.id.bt_delete_group);
        Button bt_set_group_share = (Button) mDialog.findViewById(R.id.bt_set_group_share);
        Button bt_query_group_accept_users = (Button) mDialog.findViewById(R.id.bt_query_group_accept_users);
        Button bt_change_shared_device_group = (Button) mDialog.findViewById(R.id.bt_change_shared_device_group);
        Button bt_sharer_cancel_group = (Button) mDialog.findViewById(R.id.bt_sharer_cancel_group);
        Button bt_user_cancel_share = (Button) mDialog.findViewById(R.id.bt_user_cancel_share);
        bt_user_cancel_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                cancelShareGroupByOwner();
            }
        });

        bt_sharer_cancel_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(item.getShareCode()))
                {
                    ToastUtils.showShort(getActivity(),"Not the person being shared");
                    return;
                }
                else
                {
                    mDialog.dismiss();
                    startLoading();
                    DeviceServiceFactory.getInstance().getService(IDevService.class).shareUserUnshare(item.getShareCode(), new IHttpCallBack() {
                        @Override
                        public void onSuccess(String result) {
                            finishLoading();
                            try {
                                JSONObject obj = new JSONObject(result);
                                if (obj.getInt("code") == 200) {
                                    queryGroupList();
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
            }
        });

        bt_change_shared_device_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(item.getShareCode()))
                {
                    ToastUtils.showShort(getActivity(),"Not the person being shared");
                    return;
                }
                else
                {
                    mDialog.dismiss();
                    accepterChangeGroupName(item);
                }
            }
        });
        bt_query_group_accept_users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                startLoading();
                DeviceServiceFactory.getInstance().getService(IDevService.class).deviceGroupShareUserList(item.getDgid(), new IHttpCallBack() {
                    @Override
                    public void onSuccess(String result) {
                        finishLoading();
                        System.out.println("result-:"+result);
                        ToastUtils.showShort(getActivity(),result);
                    }
                    @Override
                    public void onFail(Throwable e) {

                    }
                });
            }
        });

        bt_set_group_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                generateShareGroupInfor(item);
            }
        });

        Button bt_cancel = (Button) mDialog.findViewById(R.id.bt_cancel);
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });

        bt_queryGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                startLoading();
                DeviceServiceFactory.getInstance().getService(IDevService.class).queryDeviceGroup(item.getDgid(), new IHttpCallBack() {
                    @Override
                    public void onSuccess(String result) {
                        finishLoading();
                        try {
                            JSONObject obj = new JSONObject(result);
                            if (obj.getInt("code") == 200) {
                                ToastUtils.showShort(getActivity(),obj.getString("data"));
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

        bt_change_device_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                changeGroupDialog(item);

            }
        });

        bt_add_device_to_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                addDeviceToGroup(item);
            }
        });

        bt_query_in_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                queryDeviceInGroup(item);
            }
        });

        bt_delete_device_in_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                deleteDeviceFromGroup(item);
            }
        });

        bt_delete_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                startLoading();
                DeviceServiceFactory.getInstance().getService(IDevService.class).deleteDeviceGroup(item.getDgid(), new IHttpCallBack() {
                    @Override
                    public void onSuccess(String result) {
                      finishLoading();
                        try {
                            JSONObject obj = new JSONObject(result);
                            if (obj.getInt("code") == 200) {
                                queryGroupList();
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

        mDialog.show();
    }

    private void generateShareGroupInfor(DeviceGroupVO item)
    {
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
        TextView tv_title = (TextView) mDialog.findViewById(R.id.tv_title);
        tv_title.setText("Sharer sets device group sharing information");

        bt_generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int time = 1*24*60*60*1000;
                long useTime =   new Date().getTime();
                useTime = useTime + time;
                startLoading();
                DeviceServiceFactory.getInstance().getService(IDevService.class).shareGroupInfo(useTime,item.getDgid(),0,0,
                        new IHttpCallBack() {
                            @Override
                            public void onSuccess(String result) {
                                finishLoading();
                                //{"code":200,"msg":"","data":{"shareCode":"share_dg_C1719b382e58680e4781a68218d4e50f2277"}}
                                try {
                                    JSONObject  obj = new JSONObject(result);
                                    if (obj.getInt("code") == 200) {
                                        JSONObject infor =   obj.getJSONObject("data");
                                        tv_share_infor.setText(infor.getString("shareCode"));
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
                MyUtils.copyContentToClipboard(getActivity(),tv_share_infor.getText().toString().trim());
                ToastUtils.showShort(getActivity(),"Copy Success");
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

    private void changeGroupDialog(DeviceGroupVO item)
    {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.add_group, null);
        final Dialog mDialog = new Dialog(getActivity(), R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        EditText edit_name = (EditText) mDialog.findViewById(R.id.edit_name);
        Button bt_cancel = (Button) mDialog.findViewById(R.id.bt_cancel);
        Button bt_sure = (Button) mDialog.findViewById(R.id.bt_sure);
        TextView tv_title = (TextView) mDialog.findViewById(R.id.tv_title);
        tv_title.setText("Modify a device group");
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
                String name = MyUtils.getEditTextContent(edit_name);
                if(TextUtils.isEmpty(name))
                {
                    return;
                }
                startLoading();

                UpdateGroup updateGroup =  new UpdateGroup();
                updateGroup.setName(name);
                updateGroup.setDgid(item.getDgid());
                DeviceServiceFactory.getInstance().getService(IDevService.class).updateDeviceGroup(updateGroup,
                        new IHttpCallBack() {
                            @Override
                            public void onSuccess(String result) {
                                finishLoading();
                                try {
                                    JSONObject obj = new JSONObject(result);
                                    if (obj.getInt("code") == 200) {
                                        queryGroupList();
                                    }
                                    else
                                    {
                                        ToastUtils.showShort(getActivity(),obj.getString("msg"));
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

    private void addDeviceToGroup(DeviceGroupVO item)
    {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.add_device_to_group_dialog, null);
        final Dialog mDialog = new Dialog(getActivity(), R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        EditText edit_pk = (EditText) mDialog.findViewById(R.id.edit_pk);
        EditText edit_dk = (EditText) mDialog.findViewById(R.id.edit_dk);
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
                String pk = MyUtils.getEditTextContent(edit_pk);
                String dk = MyUtils.getEditTextContent(edit_dk);
                if(TextUtils.isEmpty(pk)||TextUtils.isEmpty(dk))
                {
                    ToastUtils.showShort(getActivity(),"Parameter cannot be empty");
                    return;
                }
                startLoading();
                List<AddDeviceParam> mList = new ArrayList();
                AddDeviceParam test1 =   new AddDeviceParam(pk,dk);
                mList.add(test1);
                DeviceServiceFactory.getInstance().getService(IDevService.class).addDeviceToGroup(item.getDgid(),mList,
                        new IHttpCallBack() {
                            @Override
                            public void onSuccess(String result) {
                                 finishLoading();
                                try {
                                    JSONObject obj = new JSONObject(result);
                                    if (obj.getInt("code") == 200) {
                                        queryGroupList();
                                        ToastUtils.showShort(getActivity(),"Added successfully");
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

    private void queryDeviceInGroup(DeviceGroupVO item)
    {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.query_device_ingroup_dialog, null);
        final Dialog mDialog = new Dialog(getActivity(), R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        EditText edit_pk = (EditText) mDialog.findViewById(R.id.edit_pk);
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
                String pk = MyUtils.getEditTextContent(edit_pk);
                if(TextUtils.isEmpty(pk))
                {
                    ToastUtils.showShort(getActivity(),"Parameter cannot be empty");
                    return;
                }
                startLoading();

                DeviceServiceFactory.getInstance().getService(IDevService.class).getGroupDeviceList(item.getDgid(),pk,1,10,
                        new IHttpCallBack() {
                            @Override
                            public void onSuccess(String result) {
                                  finishLoading();

                                  if(TextUtils.isEmpty(item.getShareCode()))
                                  {
                                      ToastUtils.showShort(getActivity(),result);
                                  }
                                  else
                                  {
                                      Intent intent = new Intent(getActivity(), SharedGroupOfDevicesActivity.class);
                                      intent.putExtra("content",result);
                                      intent.putExtra("shareCode",item.getShareCode());
                                      startActivity(intent);
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


    private void deleteDeviceFromGroup(DeviceGroupVO item)
    {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.add_device_to_group_dialog, null);
        final Dialog mDialog = new Dialog(getActivity(), R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        EditText edit_pk = (EditText) mDialog.findViewById(R.id.edit_pk);
        EditText edit_dk = (EditText) mDialog.findViewById(R.id.edit_dk);
        Button bt_cancel = (Button) mDialog.findViewById(R.id.bt_cancel);
        Button bt_sure = (Button) mDialog.findViewById(R.id.bt_sure);
        TextView tv_title = (TextView) mDialog.findViewById(R.id.tv_title);
        tv_title.setText("Remove a device from a device group");

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
                String pk = MyUtils.getEditTextContent(edit_pk);
                String dk = MyUtils.getEditTextContent(edit_dk);
                if(TextUtils.isEmpty(pk)||TextUtils.isEmpty(dk))
                {
                    ToastUtils.showShort(getActivity(),"Parameter cannot be empty");
                    return;
                }
                startLoading();
                List<AddDeviceParam> mList = new ArrayList();
                AddDeviceParam test1 =   new AddDeviceParam(pk,dk);
                mList.add(test1);
                DeviceServiceFactory.getInstance().getService(IDevService.class).deleteDeviceToGroup(item.getDgid(),mList,
                        new IHttpCallBack() {
                            @Override
                            public void onSuccess(String result) {
                                finishLoading();
                                try {
                                    JSONObject obj = new JSONObject(result);
                                    if (obj.getInt("code") == 200) {
                                        queryGroupList();
                                        ToastUtils.showShort(getActivity(),"Removed successfully");
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

    private void createReceiveGroupShare()
    {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.receiver_accept_shareinformation_dialog, null);
        final Dialog mDialog = new Dialog(getActivity(), R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        EditText edit_share_code = (EditText) mDialog.findViewById(R.id.edit_share_code);
        Button bt_cancel = (Button) mDialog.findViewById(R.id.bt_cancel);
        Button bt_sure = (Button) mDialog.findViewById(R.id.bt_sure);
        TextView tv_title = (TextView) mDialog.findViewById(R.id.tv_title);
        tv_title.setText("Accept other people's device group sharing");
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
                if(TextUtils.isEmpty(code))
                {
                    return;
                }
                startLoading();
                DeviceServiceFactory.getInstance().getService(IDevService.class).acceptDeviceGroupShare(code,
                        new IHttpCallBack() {
                            @Override
                            public void onSuccess(String result) {
                                finishLoading();
                                try {
                                    JSONObject  obj = new JSONObject(result);
                                    if (obj.getInt("code") == 200) {
                                        ToastUtils.showShort(getActivity(),"Operation successful");
                                        queryGroupList();
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


    private void accepterChangeGroupName(DeviceGroupVO item)
    {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.accepter_change_group_name_dialog, null);
        final Dialog mDialog = new Dialog(getActivity(), R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        EditText edit_name = (EditText) mDialog.findViewById(R.id.edit_name);
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
                String name = MyUtils.getEditTextContent(edit_name);
                if(TextUtils.isEmpty(name))
                {
                    ToastUtils.showShort(getActivity(),"Parameter cannot be empty");
                    return;
                }
                startLoading();
                DeviceServiceFactory.getInstance().getService(IDevService.class).shareUserSetDeviceGroupName(name,item.getShareCode(),
                        new IHttpCallBack() {
                            @Override
                            public void onSuccess(String result) {
                                finishLoading();
                                try {
                                    JSONObject obj = new JSONObject(result);
                                    if (obj.getInt("code") == 200) {
                                        queryGroupList();
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

    private void cancelShareGroupByOwner()
    {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.receiver_cancel_share_dialog, null);
        final Dialog mDialog = new Dialog(getActivity(), R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        EditText edit_code = (EditText) mDialog.findViewById(R.id.edit_code);
        TextView tv_title = (TextView) mDialog.findViewById(R.id.tv_title);
        tv_title.setText("The sharer cancels the device group sharing");
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
                if(TextUtils.isEmpty(code))
                {
                    return;
                }
                startLoading();
                DeviceServiceFactory.getInstance().getService(IDevService.class).owerUserUnshare(code,
                        new IHttpCallBack() {
                            @Override
                            public void onSuccess(String result) {
                                finishLoading();
                                try {
                                    JSONObject  obj = new JSONObject(result);
                                    if (obj.getInt("code") == 200) {
                                        ToastUtils.showShort(getActivity(),"Operation successful");
                                        queryGroupList();
                                    }
                                    else
                                    {
                                        ToastUtils.showShort(getActivity(),obj.getString("msg"));
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

}

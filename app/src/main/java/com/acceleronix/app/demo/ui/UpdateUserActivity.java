package com.acceleronix.app.demo.ui;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.acceleronix.app.demo.R;
import com.acceleronix.app.demo.adapter.LanAdapter;
import com.acceleronix.app.demo.base.BaseActivity;
import com.acceleronix.app.demo.bean.LanVO;
import com.acceleronix.app.demo.utils.MyUtils;
import com.acceleronix.app.demo.widget.BottomItemDecorationSystem;
import com.quectel.app.quecnetwork.httpservice.IHttpCallBack;
import com.quectel.app.usersdk.userservice.IUserService;
import com.quectel.app.usersdk.utils.UserServiceFactory;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class UpdateUserActivity extends BaseActivity {

    @Override
    protected int getContentLayout() {
        return R.layout.activity_list_update;
    }

    @Override
    protected void addHeadColor() {
        MyUtils.addStatusBarView(this, R.color.gray_bg);
    }

    @BindView(R.id.mList)
    RecyclerView mRecyclerView;
    LanAdapter mAdapter;

  //  List<LanVO> mList = new ArrayList<LanVO>();

    int type = -1;
    @Override
    protected void initData() {

        mRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        mRecyclerView.addItemDecoration(new BottomItemDecorationSystem(activity));
        //1 Language 2 Country 3 Time Zone
        type = getIntent().getIntExtra("type",-1);
        startLoading();
        if(type==1)
        {
            UserServiceFactory.getInstance().getService(IUserService.class).queryLanguageList(new IHttpCallBack() {
                @Override
                public void onSuccess(String result) {
                    String name = Thread.currentThread().getName();
                    System.out.println("name-1111--:"+name);
                    finishLoading();
                    try {
                        JSONObject obj = new JSONObject(result);
                        if (obj.getInt("code") == 200) {
                            Type type = new TypeToken<List<LanVO>>() {
                            }.getType();
                            List<LanVO> contentList = new Gson().fromJson(obj.getString("data"), type);

                            mAdapter = new LanAdapter(activity, contentList);
                            mAdapter.setAnimationEnable(true);
                            mRecyclerView.setAdapter(mAdapter);

                            mAdapter.setOnItemClickListener(new OnItemClickListener() {
                                @Override
                                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                                    LanVO lanVO = mAdapter.getData().get(position);
                                    Intent intent = new Intent();
                                    intent.putExtra("bean",lanVO);
                                    intent.putExtra("type",1);
                                    setResult(2222,intent);
                                    finish();

                                }
                            });

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFail(Throwable e) {

                }
            });
        }
        else if(type==2)
        {
            UserServiceFactory.getInstance().getService(IUserService.class).queryNationalityList(new IHttpCallBack() {
                @Override
                public void onSuccess(String result) {
                    finishLoading();
                    try {
                        JSONObject obj = new JSONObject(result);
                        if (obj.getInt("code") == 200) {
                            Type type = new TypeToken<List<LanVO>>() {
                            }.getType();
                            List<LanVO> contentList = new Gson().fromJson(obj.getString("data"), type);

                            mAdapter = new LanAdapter(activity, contentList);
                            mAdapter.setAnimationEnable(true);
                            mRecyclerView.setAdapter(mAdapter);
                            mAdapter.setOnItemClickListener(new OnItemClickListener() {
                                @Override
                                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                                    LanVO lanVO = mAdapter.getData().get(position);
                                    Intent intent = new Intent();
                                    intent.putExtra("bean",lanVO);
                                    intent.putExtra("type",2);
                                    setResult(2222,intent);
                                    finish();

                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFail(Throwable e) {

                }
            });
        }
        else if(type==3)
        {
            UserServiceFactory.getInstance().getService(IUserService.class).queryTimezoneList(new IHttpCallBack() {
                @Override
                public void onSuccess(String result) {
                    finishLoading();
                    try {
                        JSONObject obj = new JSONObject(result);
                        if (obj.getInt("code") == 200) {
                            Type type = new TypeToken<List<LanVO>>() {
                            }.getType();
                            List<LanVO> contentList = new Gson().fromJson(obj.getString("data"), type);

                            mAdapter = new LanAdapter(activity, contentList);
                            mAdapter.setAnimationEnable(true);
                            mRecyclerView.setAdapter(mAdapter);
                            mAdapter.setOnItemClickListener(new OnItemClickListener() {
                                @Override
                                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                                    LanVO lanVO = mAdapter.getData().get(position);
                                    Intent intent = new Intent();
                                    intent.putExtra("bean",lanVO);
                                    intent.putExtra("type",3);
                                    setResult(2222,intent);
                                    finish();

                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFail(Throwable e) {

                }
            });
        }


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

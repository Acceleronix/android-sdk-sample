package com.quectel.app.demo.ui;

import android.Manifest;
import android.os.Build;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.permissionx.guolindev.PermissionMediator;
import com.permissionx.guolindev.callback.RequestCallback;
import com.quectel.app.demo.R;
import com.quectel.app.demo.base.BaseActivity;
import com.quectel.app.demo.bean.UserInfor;
import com.quectel.app.demo.fragment.MainFragment;
import com.quectel.app.demo.utils.MyUtils;
import com.quectel.app.quecnetwork.httpservice.IHttpCallBack;
import com.quectel.app.quecnetwork.logservice.ILogService;
import com.quectel.app.quecnetwork.utils.LogService;
import com.quectel.app.usersdk.userservice.IUserService;
import com.quectel.app.usersdk.utils.UserServiceFactory;
import com.quectel.basic.permission.QuecPermission;
import com.quectel.basic.quecmmkv.MmkvManager;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import io.reactivex.functions.Consumer;
import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

public class HomeActivity  extends BaseActivity {
    private static final String[] permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? new String[]{
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION,
    } : new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
    };


    @Override
    protected int getContentLayout() {
        return R.layout.activity_home;
    }

    @Override
    protected void addHeadColor() {
        MyUtils.addStatusBarView(this,R.color.gray_bg);
    }

    @Override
    protected void initData() {

        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .subscribe(new Consumer<Boolean>() {
                               @Override
                               public void accept(Boolean grant) throws Exception {
                               }
                           }

                );


        if (findFragment(MainFragment.class) == null) {
            loadRootFragment(R.id.fl_container, MainFragment.newInstance());
        }

        queryUserInfor();

        PermissionMediator permissionMediator = QuecPermission.init(this);
        permissionMediator.permissions(permission).request(new RequestCallback() {
            @Override
            public void onResult(boolean allGranted, @NonNull List<String> grantedList, @NonNull List<String> deniedList) {

            }
        });

    }

    @Override
    public void onBackPressedSupport() {
        super.onBackPressedSupport();
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultHorizontalAnimator();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogService.get(ILogService.class).closeLog();

    }


    private void queryUserInfor()
    {
        UserServiceFactory.getInstance().getService(IUserService.class).queryUserInfo(
                new IHttpCallBack() {
                    @Override
                    public void onSuccess(String result) {
                        finishLoading();
                        UserInfor  user = new Gson().fromJson(result, UserInfor.class);
                        if(user.getCode()==200)
                        {
                            UserInfor.DataDTO userInfor = user.getData();
                            MmkvManager.getInstance().put("uid", userInfor.getUid());
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

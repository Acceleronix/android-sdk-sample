package com.acceleronix.app.demo.ui;

import android.content.Intent;
import android.view.View;

import com.acceleronix.app.demo.R;
import com.acceleronix.app.demo.base.BaseActivity;
import com.acceleronix.app.demo.utils.MyUtils;

import butterknife.OnClick;

public class SelectLoginActivity extends BaseActivity {

    @Override
    protected int getContentLayout() {
        return R.layout.activity_select_login;
    }

    @Override
    protected void addHeadColor() {
        MyUtils.addStatusBarView(this,R.color.gray_bg);
    }
    @Override
    protected void initData() {

    }
    @OnClick({R.id.iv_back,R.id.bt_login1,R.id.bt_login2,R.id.bt_login3})
    public void onViewClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;

            case R.id.bt_login1:

                 intent = new Intent(activity, LoginActivity.class);
                 startActivity(intent);
                 finish();

                break;

            case R.id.bt_login2:

                intent = new Intent(activity, EmailLoginActivity.class);
                startActivity(intent);
                finish();

                break;

            case R.id.bt_login3:

                intent = new Intent(activity, AuthCodeLoginActivity.class);
                startActivity(intent);
                finish();
                break;

        }
    }




}

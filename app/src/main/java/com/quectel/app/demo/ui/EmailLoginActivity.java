package com.quectel.app.demo.ui;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.widget.AppCompatEditText;

import com.quectel.app.demo.R;
import com.quectel.app.demo.base.BaseActivity;
import com.quectel.app.demo.constant.CloudConfig;
import com.quectel.app.demo.utils.MyUtils;
import com.quectel.app.demo.utils.SPUtils;
import com.quectel.app.demo.utils.ToastUtils;
import com.quectel.app.quecnetwork.httpservice.IResponseCallBack;
import com.quectel.app.usersdk.userservice.IUserService;
import com.quectel.app.usersdk.utils.UserServiceFactory;
import com.quectel.sdk.iot.QuecIotAppSdk;

import butterknife.BindView;
import butterknife.OnClick;

public class EmailLoginActivity extends BaseActivity {


    @Override
    protected int getContentLayout() {
        return R.layout.activity_email_login;
    }

    @Override
    protected void addHeadColor() {
        MyUtils.addStatusBarView(this, R.color.gray_bg);
    }


    @BindView(R.id.edit_email)
    EditText edit_email;

    @BindView(R.id.edit_pass)
    EditText edit_pass;

    @BindView(R.id.edit_countryCode)
    AppCompatEditText editCode;

    @Override
    protected void initData() {


    }

    @OnClick({R.id.iv_back, R.id.bt_login, R.id.tv_register_mail})
    public void onViewClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;

            case R.id.bt_login:
                //pass-:aA654321
                System.out.println("bt_login email");
                String email = MyUtils.getEditTextContent(edit_email);
                String pass = MyUtils.getEditTextContent(edit_pass);
                System.out.println("str1-:" + email);
                System.out.println("pass-:" + pass);
                if (TextUtils.isEmpty(email)) {
                    ToastUtils.showLong(activity, "Please enter your email address");
                    return;
                }
                if (TextUtils.isEmpty(pass)) {
                    ToastUtils.showLong(activity, "Please enter your password");
                    return;
                }

                UserServiceFactory.getInstance().getService(IUserService.class).emailPwdLogin(email,
                        pass,
                        new IResponseCallBack() {
                            @Override
                            public void onSuccess() {
                                ToastUtils.showShort(activity, "Login successful");
                                edit_email.setText("");
                                String countryCode = MyUtils.getEditTextContent(editCode);
                                //私有云不用设置国际码，QuecPublicConfigBean已经包含了mcc
                                if (!SPUtils.getBoolean(getMyActivity(), CloudConfig.IS_CUSTOM_CLOUD, false)){
                                    setCountryCode(countryCode);
                                }
                                startActivity(new Intent(activity, HomeActivity.class));
                                finish();
                            }

                            @Override
                            public void onFail(Throwable e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onError(int code, String errorMsg) {
                                ToastUtils.showLong(activity, errorMsg);

                            }
                        }
                );

                break;

            case R.id.tv_register_mail:

                intent = new Intent(activity, EmailRegisterActivity.class);
                startActivity(intent);

                break;

        }
    }


    private void setCountryCode(String countryCode) {
        QuecIotAppSdk.getInstance().setCountryCode(countryCode);
    }
}

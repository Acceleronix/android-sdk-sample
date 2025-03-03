package com.acceleronix.app.demo.ui;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.acceleronix.app.demo.R;
import com.acceleronix.app.demo.base.BaseActivity;
import com.acceleronix.app.demo.utils.MyUtils;
import com.acceleronix.app.demo.utils.ToastUtils;
import com.quectel.app.quecnetwork.httpservice.IHttpCallBack;
import com.quectel.app.usersdk.constant.UserConstant;
import com.quectel.app.usersdk.userservice.IUserService;
import com.quectel.app.usersdk.utils.UserServiceFactory;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

public class EmailRegisterActivity extends BaseActivity {

    @Override
    protected int getContentLayout() {
        return R.layout.activity_email_register;
    }

    @Override
    protected void addHeadColor() {
        MyUtils.addStatusBarView(this,R.color.gray_bg);
    }

    @BindView(R.id.edit_email)
    EditText edit_email;

    @BindView(R.id.edit_pass)
    EditText edit_pass;

    @BindView(R.id.edit_yanzheng)
    EditText edit_yanzheng;

    @BindView(R.id.bt_getCode)
    Button bt_getCode;


    @Override
    protected void initData() {


    }

    @OnClick({R.id.iv_back, R.id.bt_register,R.id.bt_getCode})
    public void onViewClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.bt_getCode:

                    String mail = MyUtils.getEditTextContent(edit_email);
                    if (TextUtils.isEmpty(mail)) {
                        ToastUtils.showShort(activity, "Please enter your email address");
                        return;
                    }

                    startLoading();
                    UserServiceFactory.getInstance().getService(IUserService.class).sendV2EmailCode(
                            mail, UserConstant.TYPE_SEND_EMAIL_REGISTER,new IHttpCallBack(){
                                @Override
                                public void onSuccess(String result) {
                                   finishLoading();
                                    try {
                                        JSONObject  obj = new JSONObject(result);
                                        if (obj.getInt("code") == 200) {
                                             ToastUtils.showShort(activity,"Verification code sent");
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

                break;

            case R.id.iv_back:
                finish();
              break;

            case R.id.bt_register:

                String email = MyUtils.getEditTextContent(edit_email);
                if(TextUtils.isEmpty(email))
                {
                    ToastUtils.showLong(activity,"Please enter your email address");
                    return;
                }
                String pass = MyUtils.getEditTextContent(edit_pass);
                if(TextUtils.isEmpty(pass))
                {
                    ToastUtils.showLong(activity,"Please enter your password");
                    return;
                }

                String verifyCode = MyUtils.getEditTextContent(edit_yanzheng);
                if(TextUtils.isEmpty(verifyCode))
                {
                    ToastUtils.showLong(activity,"Please enter the verification code");
                    return;
                }

                //Email Registration
                UserServiceFactory.getInstance().getService(IUserService.class).emailPwdRegister(
                        verifyCode,email,pass,0,0,0,
                        new IHttpCallBack() {
                            @Override
                            public void onSuccess(String result) {
                                try {
                                    JSONObject  obj = new JSONObject(result);
                                    if (obj.getInt("code") == 200) {
                                        ToastUtils.showShort(activity,"Successful registration");
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

                break;

        }

    }

}

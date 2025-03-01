package com.quectel.app.demo.ui;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.quectel.app.demo.R;
import com.quectel.app.demo.base.BaseActivity;
import com.quectel.app.demo.utils.MyUtils;
import com.quectel.app.demo.utils.ToastUtils;
import com.quectel.app.quecnetwork.httpservice.IHttpCallBack;
import com.quectel.app.usersdk.constant.UserConstant;
import com.quectel.app.usersdk.userservice.IUserService;
import com.quectel.app.usersdk.utils.UserServiceFactory;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

public class ResetPasswordByEmailActivity extends BaseActivity {

    @Override
    protected int getContentLayout() {
        return R.layout.activity_reset_pass_email;
    }

    @Override
    protected void addHeadColor() {
        MyUtils.addStatusBarView(this,R.color.gray_bg);
    }

    @BindView(R.id.edit_email)
    EditText edit_email;

    @BindView(R.id.rl_three)
    RelativeLayout rl_three;

    @BindView(R.id.edit_yanzheng)
    EditText edit_yanzheng;

    @BindView(R.id.bt_getCode)
    Button bt_getCode;


    private static final int INTERVAL = 2000;
    private long mExitTime;

    @Override
    protected void initData() {

    }

    @OnClick({R.id.iv_back,R.id.bt_getCode,R.id.bt_reset})
    public void onViewClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.bt_getCode:
                if (System.currentTimeMillis() - mExitTime > INTERVAL)
                {
                    String email = MyUtils.getEditTextContent(edit_email);
                    if (TextUtils.isEmpty(email)) {
                        ToastUtils.showShort(activity, "Please enter your email address");
                        return;
                    }

                    startLoading();
                    UserServiceFactory.getInstance().getService(IUserService.class).sendV2EmailCode(email, UserConstant.TYPE_SEND_EMAIL_RESET_PASSWORD,new IHttpCallBack() {
                                @Override
                                public void onSuccess(String result) {
                                    //{"code":200,"msg":"","data":null}
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

                    mExitTime = System.currentTimeMillis();

                }
                 break;

            case R.id.iv_back:
                finish();
              break;

            case R.id.bt_reset:

                String email = MyUtils.getEditTextContent(edit_email);
                String code = MyUtils.getEditTextContent(edit_yanzheng);
                System.out.println("str1-:"+email);
                System.out.println("verifyCode-:"+code);
                if(TextUtils.isEmpty(email))
                {
                    ToastUtils.showLong(activity,"Please enter your email address");
                    return;
                }

                if(TextUtils.isEmpty(code))
                {
                    ToastUtils.showLong(activity,"Please enter the verification code");
                    return;
                }
                String password = "aA123456";
                UserServiceFactory.getInstance().getService(IUserService.class).userPwdResetByEmail("",code,
                        email,password, new IHttpCallBack() {
                            @Override
                            public void onSuccess(String result) {
                                //{"code":200,"msg":"","data":"密码已重置为 12345678"}
                                try {
                                    JSONObject  obj = new JSONObject(result);
                                    if (obj.getInt("code") == 200) {
                                       // ToastUtils.showShort(activity,"密码成功重置为12345678");
                                        finish();
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

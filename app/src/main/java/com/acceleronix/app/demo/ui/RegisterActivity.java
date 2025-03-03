package com.acceleronix.app.demo.ui;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
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

public class RegisterActivity extends BaseActivity {

    @Override
    protected int getContentLayout() {
        return R.layout.activity_register;
    }

    @Override
    protected void addHeadColor() {
        MyUtils.addStatusBarView(this,R.color.gray_bg);
    }

    @BindView(R.id.edit_phone)
    EditText edit_phone;

    @BindView(R.id.edit_pass)
    EditText edit_pass;

    @BindView(R.id.edit_yanzheng)
    EditText edit_yanzheng;

    @BindView(R.id.bt_getCode)
    Button bt_getCode;


    Handler handler;
    int countNum = 0;
    private static final int INTERVAL = 2000;
    private long mExitTime;
    @Override
    protected void initData() {

        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                int  dCount = msg.arg1;
                if (dCount == 0)
                {
                    bt_getCode.setText("Get verification code");
                    bt_getCode.setEnabled(true);
                }
                else
                {
                    bt_getCode.setEnabled(false);
                    bt_getCode.setText("(" + dCount + ")second");
                }
            }
        };

    }

    private Runnable dRunnable = new Runnable() {
        @Override
        public void run() {
            countNum = countNum - 1;
            Message msg = new Message();
            msg.arg1 = countNum;
            handler.sendMessage(msg);
            if(countNum==0)
            {
                handler.removeCallbacks(dRunnable);
                return;
            }
            handler.postDelayed(dRunnable,1000);
        }
    };

    @OnClick({R.id.iv_back, R.id.bt_register,R.id.bt_getCode})
    public void onViewClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.bt_getCode:
                if (System.currentTimeMillis() - mExitTime > INTERVAL)
                {
                    String phoneContent = MyUtils.getEditTextContent(edit_phone);
                    if (TextUtils.isEmpty(phoneContent)) {
                        ToastUtils.showShort(activity, "Please enter your mobile number");
                        return;
                    }

                    UserServiceFactory.getInstance().getService(IUserService.class).sendV2PhoneSmsCode(
                            "86",phoneContent, UserConstant.TYPE_SMS_CODE_REGISTER, new IHttpCallBack(){
                                @Override
                                public void onSuccess(String result) {
                                    System.out.println("sendPhoneSmsCode onSuccess-:"+result);
                                    try {
                                        JSONObject  obj = new JSONObject(result);
                                        if (obj.getInt("code") == 200) {
                                            countNum = 60;
                                            handler.postDelayed(dRunnable,1000);
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

            case R.id.bt_register:
                System.out.println("bt_register");

                // intent = new Intent(getActivity(), LoginActivity.class);
                //  startActivity(intent);

                String phone = MyUtils.getEditTextContent(edit_phone);
                if(TextUtils.isEmpty(phone))
                {
                    ToastUtils.showLong(activity,"Please enter your mobile number");
                    return;
                }
                String pass = MyUtils.getEditTextContent(edit_pass);
                if(TextUtils.isEmpty(pass))
                {
                    ToastUtils.showLong(activity,"Please enter your password");
                    return;
                }
                System.out.println("str1-:"+phone);
                System.out.println("pass-:"+pass);
                String verifyCode = MyUtils.getEditTextContent(edit_yanzheng);
                System.out.println("verifyCode--:"+verifyCode);
                if(TextUtils.isEmpty(verifyCode))
                {
                    ToastUtils.showLong(activity,"Please enter the verification code");
                    return;
                }

                UserServiceFactory.getInstance().getService(IUserService.class).phonePwdRegister(
                        phone, pass, verifyCode,
                        "", "", "", "",
                        new IHttpCallBack() {
                            @Override
                            public void onSuccess(String result) {
                                 System.out.println("-phoneRegister-onSuccess:"+result);
                                try {
                                    JSONObject  obj = new JSONObject(result);
                                    if (obj.getInt("code") == 200) {
                                        ToastUtils.showShort(activity,"Successful registration");
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


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(handler!=null)
        {
            handler.removeCallbacks(dRunnable);
        }

    }




}

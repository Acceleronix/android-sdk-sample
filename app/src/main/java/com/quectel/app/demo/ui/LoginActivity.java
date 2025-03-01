package com.quectel.app.demo.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;

import com.google.gson.Gson;
import com.quectel.app.demo.R;
import com.quectel.app.demo.base.BaseActivity;
import com.quectel.app.demo.constant.CloudConfig;
import com.quectel.app.demo.dialog.ServiceTypeCustomDialog;
import com.quectel.app.demo.dialog.ServiceTypeDialog;
import com.quectel.app.demo.utils.MyUtils;
import com.quectel.app.demo.utils.SPUtils;
import com.quectel.app.demo.utils.ToastUtils;
import com.quectel.app.quecnetwork.httpservice.IHttpCallBack;
import com.quectel.app.quecnetwork.httpservice.IResponseCallBack;
import com.quectel.app.usersdk.constant.UserConstant;
import com.quectel.app.usersdk.userservice.IUserService;
import com.quectel.app.usersdk.utils.UserServiceFactory;
import com.quectel.sdk.iot.QuecCloudServiceType;
import com.quectel.sdk.iot.QuecIotAppSdk;
import com.quectel.sdk.iot.bean.QuecPublicConfigBean;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

public class LoginActivity extends BaseActivity {
    public static final int QuecCloudServiceTypeChina =0;
    public static final int QuecCloudServiceTypeEurope =1;
    public static final int QuecCloudServiceTypeNorthAmerica =2;

    private int serviceType = QuecCloudServiceTypeChina;

    @Override
    protected void onResume() {
        super.onResume();
        if(serviceType==QuecCloudServiceTypeChina){
            rbChangeServiceTypeQuec.setText("Data Center-China");
        }else if(serviceType==QuecCloudServiceTypeEurope){
            rbChangeServiceTypeQuec.setText("Data Center-Europe");
        }else if(serviceType==QuecCloudServiceTypeNorthAmerica){
            rbChangeServiceTypeQuec.setText("Data Center-America");
        }
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_login;
    }

    @Override
    protected void addHeadColor() {
        MyUtils.addStatusBarView(this, R.color.gray_bg);
    }

    @BindView(R.id.edit_phone)
    EditText edit_phone;

    @BindView(R.id.edit_pass)
    EditText edit_pass;

    @BindView(R.id.rl_two)
    RelativeLayout rl_two;

    @BindView(R.id.rl_three)
    RelativeLayout rl_three;

    boolean loginStyle = false;

    @BindView(R.id.bt_style)
    Button bt_style;

    @BindView(R.id.edit_yanzheng)
    EditText edit_yanzheng;

    @BindView(R.id.bt_getCode)
    Button bt_getCode;

    @BindView(R.id.edit_countryCode)
    AppCompatEditText editCode;

    @BindView(R.id.rbChangeServiceTypeQuec)
    RadioButton rbChangeServiceTypeQuec;

    @BindView(R.id.rbChangeServiceTypeCustom)
    RadioButton rbChangeServiceTypeCustom;

    @BindView(R.id.tvConfigText)
    TextView tvConfigText;


    Handler handler;
    int countNum = 0;
    private static final int INTERVAL = 2000;
    private long mExitTime;

    @Override
    protected void initData() {

        bt_style.setText("Verification code login");
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                int dCount = msg.arg1;
                if (dCount == 0) {
                    bt_getCode.setText("Get verification code");
                    bt_getCode.setEnabled(true);
                } else {
                    bt_getCode.setEnabled(false);
                    bt_getCode.setText("(" + dCount + ")秒");
                }
            }
        };

        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.MANAGE_EXTERNAL_STORAGE
                )
                .subscribe(new Consumer<Boolean>() {
                               @Override
                               public void accept(Boolean grant) throws Exception {
                                   if(grant)
                                   {

                                   }
                                   else
                                   {

                                   }

                               }
                           }

                );


    }

    private Runnable dRunnable = new Runnable() {
        @Override
        public void run() {
            countNum = countNum - 1;
            Message msg = new Message();
            msg.arg1 = countNum;
            handler.sendMessage(msg);
            if (countNum == 0) {
                handler.removeCallbacks(dRunnable);
                return;
            }
            handler.postDelayed(dRunnable, 1000);
        }
    };


    @OnClick({R.id.iv_back, R.id.bt_login, R.id.tv_register, R.id.bt_style, R.id.bt_getCode,R.id.rbChangeServiceTypeQuec,R.id.rbChangeServiceTypeCustom,R.id.btEmailLogin})
    public void onViewClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.btEmailLogin:
                intent = new Intent(activity, EmailLoginActivity.class);
                startActivity(intent);
                break;
            case R.id.rbChangeServiceTypeQuec:
                rbChangeServiceTypeCustom.setTextColor(R.color.gray_btn);
                rbChangeServiceTypeQuec.setTextColor(R.color.new_lan);
                tvConfigText.setVisibility(View.GONE);
                ServiceTypeDialog dialog = new ServiceTypeDialog(this);
                dialog.setOnConfirmClickListener(new ServiceTypeDialog.OnConfirmClickListener() {
                    @Override
                    public void onConfirm(QuecCloudServiceType type) {
                        switch (type){
                            case QuecCloudServiceTypeChina:
                                QuecIotAppSdk.getInstance().startWithUserDomain(getApplication(),"C.DM.5903.1", "EufftRJSuWuVY7c6txzGifV9bJcfXHAFa7hXY5doXSn7", QuecCloudServiceType.QuecCloudServiceTypeChina);
                                rbChangeServiceTypeQuec.setText("Data Center-China");
                                serviceType=0;
                                SPUtils.putBoolean(getMyActivity(), CloudConfig.IS_CUSTOM_CLOUD, false);
                                SPUtils.putInt(getMyActivity(), CloudConfig.QUEC_ClOUD_SERVICE_TYPE, serviceType);
                                SPUtils.putString(getMyActivity(), CloudConfig.QUEC_USER_DOMAIN, "C.DM.5903.1");
                                SPUtils.putString(getMyActivity(), CloudConfig.QUEC_DOMAIN_SECRET, "EufftRJSuWuVY7c6txzGifV9bJcfXHAFa7hXY5doXSn7");
                                break;
                            case QuecCloudServiceTypeEurope:
                                QuecIotAppSdk.getInstance().startWithUserDomain(LoginActivity.this.getApplication(),"E.SP.4294967410", "3aRNUwWahjyANa7WfBK2wCCkxCexB6nXxKJwXxfePvzf", QuecCloudServiceType.QuecCloudServiceTypeEurope);
                                rbChangeServiceTypeQuec.setText("Data Center-Europe");
                                serviceType=1;
                                SPUtils.putBoolean(getMyActivity(), CloudConfig.IS_CUSTOM_CLOUD, false);
                                SPUtils.putInt(getMyActivity(), CloudConfig.QUEC_ClOUD_SERVICE_TYPE, serviceType);
                                SPUtils.putString(getMyActivity(), CloudConfig.QUEC_USER_DOMAIN, "E.SP.4294967410");
                                SPUtils.putString(getMyActivity(), CloudConfig.QUEC_DOMAIN_SECRET, "3aRNUwWahjyANa7WfBK2wCCkxCexB6nXxKJwXxfePvzf");
                                break;
                            case QuecCloudServiceTypeNorthAmerica:
                                QuecIotAppSdk.getInstance().startWithUserDomain(LoginActivity.this.getApplication(),"U.SP.8589934603", "pUTp5goB1bLinprRQMmK3EPiiuPiGrJtKUNptWRXVmP", QuecCloudServiceType.QuecCloudServiceTypeNorthAmerica);
                                rbChangeServiceTypeQuec.setText("Data Center-America");
                                serviceType=2;
                                SPUtils.putBoolean(getMyActivity(), CloudConfig.IS_CUSTOM_CLOUD, false);
                                SPUtils.putInt(getMyActivity(), CloudConfig.QUEC_ClOUD_SERVICE_TYPE, serviceType);
                                SPUtils.putString(getMyActivity(), CloudConfig.QUEC_USER_DOMAIN, "U.SP.8589934603");
                                SPUtils.putString(getMyActivity(), CloudConfig.QUEC_DOMAIN_SECRET, "pUTp5goB1bLinprRQMmK3EPiiuPiGrJtKUNptWRXVmP");
                                break;
                        }
                    }
                });
                dialog.show();
                break;
            case R.id.rbChangeServiceTypeCustom:
                rbChangeServiceTypeCustom.setTextColor(R.color.new_lan);
                rbChangeServiceTypeQuec.setTextColor(R.color.gray_btn);
                tvConfigText.setVisibility(View.VISIBLE);
                ServiceTypeCustomDialog customDialog = new ServiceTypeCustomDialog(this);
                customDialog.setOnConfirmClickListener(new ServiceTypeCustomDialog.OnServiceTypeCustomConfirmClickListener() {
                    @Override
                    public void onServiceTypeCustomConfirm(QuecPublicConfigBean bean) {
                        tvConfigText.setText(bean.toString());
                        QuecIotAppSdk.getInstance().startWithQuecPublicConfigBean(getApplication(), bean);
                        SPUtils.putBoolean(getMyActivity(), CloudConfig.IS_CUSTOM_CLOUD, true);
                        SPUtils.putString(getMyActivity(), CloudConfig.PUBLIC_CONFIG_BEAN, new Gson().toJson( bean));
                    }
                });
                customDialog.show();
                break;
            case R.id.bt_getCode:
                if (System.currentTimeMillis() - mExitTime > INTERVAL) {
                    String phoneContent = MyUtils.getEditTextContent(edit_phone);
                    if (TextUtils.isEmpty(phoneContent)) {
                        ToastUtils.showShort(activity, "Please enter your mobile number");
                        return;
                    }

                    String countryCode = MyUtils.getEditTextContent(editCode);
                    String resolveCode = "";
                    if (countryCode.startsWith("+")) {
                        resolveCode = countryCode.replace("+", "");
                    } else {
                        resolveCode = countryCode;
                    }

                    UserServiceFactory.getInstance().getService(IUserService.class).sendV2PhoneSmsCode(
                            resolveCode, phoneContent, UserConstant.TYPE_SMS_CODE_LOGIN, new IHttpCallBack() {
                                @Override
                                public void onSuccess(String result) {
                                    System.out.println("sendPhoneSmsCode onSuccess-:" + result);

                                    try {
                                        JSONObject obj = new JSONObject(result);
                                        if (obj.getInt("code") == 200) {
                                            countNum = 60;
                                            handler.postDelayed(dRunnable, 1000);
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

            case R.id.bt_style:

                if (!loginStyle) {
                    bt_style.setText("Password login");
                    loginStyle = true;

                    rl_three.setVisibility(View.VISIBLE);
                    rl_two.setVisibility(View.GONE);

                } else {
                    bt_style.setText("Verification code login");
                    loginStyle = false;
                    rl_three.setVisibility(View.GONE);
                    rl_two.setVisibility(View.VISIBLE);
                }

                break;

            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_register:
                System.out.println("tv_register");
                intent = new Intent(activity, RegisterActivity.class);
                startActivity(intent);

                break;

            case R.id.bt_login:
                System.out.println("bt_login");
                String phone = MyUtils.getEditTextContent(edit_phone);
                String pass = MyUtils.getEditTextContent(edit_pass);
                System.out.println("str1-:" + phone);
                System.out.println("pass-:" + pass);
                if (TextUtils.isEmpty(phone)) {
                    ToastUtils.showLong(activity, "Please enter your mobile number");
                    return;
                }


                if (!loginStyle) {
                    if (TextUtils.isEmpty(pass)) {
                        ToastUtils.showLong(activity, "Please enter your password");
                        return;
                    }

                    String countryCode = MyUtils.getEditTextContent(editCode);
                    String resolveCode = "";
                    if (countryCode.startsWith("+")) {
                        resolveCode = countryCode.replace("+", "");
                    } else {
                        resolveCode = countryCode;
                    }

                    UserServiceFactory.getInstance().getService(IUserService.class).phonePwdLogin(
                            phone, pass, resolveCode, new IResponseCallBack() {
                                @Override
                                public void onSuccess() {
                                    Log.i("renbao","--onSuccess-phoneLogin-");
                                    ToastUtils.showShort(activity, "Login successful");
                                    edit_phone.setText("");

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
                                    ToastUtils.showShort(activity, errorMsg);
                                }
                            }
                    );
                } else {

                    String verifyCode = MyUtils.getEditTextContent(edit_yanzheng);
                    System.out.println("verifyCode--:" + verifyCode);
                    if (TextUtils.isEmpty(verifyCode)) {
                        ToastUtils.showLong(activity, "Please enter the verification code");
                        return;
                    }

                    String countryCode = MyUtils.getEditTextContent(editCode);
                    String resolveCode = "";
                    if (countryCode.startsWith("+")) {
                        resolveCode = countryCode.replace("+", "");
                    } else {
                        resolveCode = countryCode;
                    }
                    UserServiceFactory.getInstance().getService(IUserService.class).phoneSmsCodeLogin(phone,
                            verifyCode, resolveCode, new IResponseCallBack() {
                                @Override
                                public void onSuccess() {
                                    System.out.println("login success Sms");
                                    ToastUtils.showShort(activity, "Login successful");
                                    edit_phone.setText("");

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
                                    ToastUtils.showShort(activity, errorMsg);
                                }
                            }

                    );

                }

                break;

        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (handler != null) {
            handler.removeCallbacks(dRunnable);
        }

    }


    private void setCountryCode(String countryCode) {
        QuecIotAppSdk.getInstance().setCountryCode(countryCode);
    }
}

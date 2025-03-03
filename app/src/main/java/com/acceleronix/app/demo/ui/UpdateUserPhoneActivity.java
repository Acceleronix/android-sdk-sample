package com.acceleronix.app.demo.ui;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
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

public class UpdateUserPhoneActivity extends BaseActivity {

    @Override
    protected int getContentLayout() {
        return R.layout.activity_update_user_phone;
    }

    @Override
    protected void addHeadColor() {
        MyUtils.addStatusBarView(this,R.color.gray_bg);
    }

    @BindView(R.id.edit_new_phone)
    EditText edit_new_phone;

    @BindView(R.id.edit_old_phone)
    EditText edit_old_phone;

    @BindView(R.id.edit_yanzheng)
    EditText edit_yanzheng;

    @BindView(R.id.edit_yanzheng2)
    EditText edit_yanzheng2;


    @Override
    protected void initData() {

    }

    @OnClick({R.id.iv_back,R.id.bt_getCode,R.id.bt_sure,R.id.bt_getCode2})
    public void onViewClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.bt_getCode:
                    String newPhone = MyUtils.getEditTextContent(edit_new_phone);
                    if (TextUtils.isEmpty(newPhone)) {
                        ToastUtils.showShort(activity, "Please enter your mobile number");
                        return;
                    }
                    startLoading();

                                UserServiceFactory.getInstance().getService(IUserService.class).sendV2PhoneSmsCode(
                        "86",newPhone, UserConstant.TYPE_SMS_CODE_REGISTER,new IHttpCallBack(){
                            @Override
                            public void onSuccess(String result) {
                                finishLoading();
                                ToastUtils.showShort(activity,"Verification code has been sent");
                            }
                            @Override
                            public void onFail(Throwable e) {
                                  e.printStackTrace();
                            }
                        }
                );
                 break;
            case R.id.bt_getCode2:
                String oldPhone = MyUtils.getEditTextContent(edit_old_phone);
                if (TextUtils.isEmpty(oldPhone)) {
                    ToastUtils.showShort(activity, "Please enter your mobile number");
                    return;
                }
                startLoading();

                UserServiceFactory.getInstance().getService(IUserService.class).sendV2PhoneSmsCode(
                        "86",oldPhone,UserConstant.TYPE_SMS_CODE_LOGOFF, new IHttpCallBack(){
                            @Override
                            public void onSuccess(String result) {
                                finishLoading();
                                ToastUtils.showShort(activity,"Verification code has been sent");
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

            case R.id.bt_sure:
                String phone1 = MyUtils.getEditTextContent(edit_new_phone);
                String phone2 = MyUtils.getEditTextContent(edit_old_phone);
                String code = MyUtils.getEditTextContent(edit_yanzheng);
                String code2 = MyUtils.getEditTextContent(edit_yanzheng2);

                if(TextUtils.isEmpty(phone1))
                {
                    ToastUtils.showLong(activity,"Please enter your new phone number");
                    return;
                }
                if(TextUtils.isEmpty(phone2))
                {
                    ToastUtils.showLong(activity,"Please enter your mobile number");
                    return;
                }

                if(TextUtils.isEmpty(code))
                {
                    ToastUtils.showLong(activity,"Please enter the verification code");
                    return;
                }
                if(TextUtils.isEmpty(code2))
                {
                    ToastUtils.showLong(activity,"Please enter the verification code");
                    return;
                }

                UserServiceFactory.getInstance().getService(IUserService.class).updatePhone("86", phone1, code,
                        "86", phone2, code2, new IHttpCallBack() {
                            @Override
                            public void onSuccess(String result) {
                                //{"code":200,"msg":"","data":null}
                                System.out.println("updatePhone---:" + result);

                                try {
                                    JSONObject  obj = new JSONObject(result);
                                    if (obj.getInt("code") == 200) {
                                        ToastUtils.showLong(activity,"Mobile number modified successfully");
                                           finish();
                                    }
                                    else
                                    {
                                        ToastUtils.showLong(activity,obj.getString("msg"));
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

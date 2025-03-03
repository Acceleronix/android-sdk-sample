package com.acceleronix.app.demo.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.acceleronix.app.demo.R;
import com.acceleronix.app.demo.bean.LanVO;
import com.acceleronix.app.demo.bean.UserInfor;
import com.acceleronix.app.demo.fragmentbase.BaseMainFragment;
import com.acceleronix.app.demo.ui.LoginActivity;
import com.acceleronix.app.demo.ui.ResetPasswordByEmailActivity;
import com.acceleronix.app.demo.ui.ResetPasswordByPhoneActivity;
import com.acceleronix.app.demo.ui.UpdateUserActivity;
import com.acceleronix.app.demo.ui.UpdateUserPhoneActivity;
import com.acceleronix.app.demo.utils.DensityUtils;
import com.acceleronix.app.demo.utils.MyUtils;
import com.acceleronix.app.demo.utils.ToastUtils;
import com.quectel.app.device.iot.IotChannelController;
import com.quectel.app.quecnetwork.httpservice.IHttpCallBack;
import com.quectel.app.usersdk.userservice.IUserService;
import com.quectel.app.usersdk.utils.UserServiceFactory;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;


public class MineFragment extends BaseMainFragment {

    public static synchronized MineFragment newInstance() {
        MineFragment frag = new MineFragment();
        return frag;
    }
    @Override
    protected void getNeedArguments(Bundle bundle) {

    }
    @BindView(R.id.tv_nickname)
    TextView tv_nickname;

    @BindView(R.id.tv_phone)
    TextView tv_phone;

    @BindView(R.id.tv_address)
    TextView tv_address;

    @BindView(R.id.tv_sex)
    TextView tv_sex;

    @BindView(R.id.tv_lan)
    TextView tv_lan;

    @BindView(R.id.tv_country)
    TextView tv_country;

    @BindView(R.id.tv_timezone)
    TextView tv_timezone;

    @BindView(R.id.tv_email)
    TextView tv_email;

    @BindView(R.id.civ_head)
    ImageView civ_head;


    @Override
    protected int getLayoutView() {
        return R.layout.mine_layout;
    }

    @Override
    protected void processBusiness() {
    }

    @Override
    public void onResume() {
        super.onResume();
        queryUserInfor();

    }

    private static final int REQUEST_CODE = 1000;
    private static final int OPEN_PHOTO_REQUEST_CODE = 1001;
    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState)
    {
        super.onLazyInitView(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            if (Environment.isExternalStorageManager())
            {
                System.out.println("have permission");
            }
            else
            {
                requestManageAppAllFilePermission(getActivity());
            }
        }

    }

    public  void requestManageAppAllFilePermission(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        startActivityForResult(intent, REQUEST_CODE);
    }


    UserInfor user = null;
    private void queryUserInfor()
    {
        startLoading();
        UserServiceFactory.getInstance().getService(IUserService.class).queryUserInfo(
                new IHttpCallBack() {
                    @Override
                    public void onSuccess(String result) {
                        finishLoading();
                         user = new Gson().fromJson(result, UserInfor.class);
                        if(user.getCode()==200)
                        {
                            UserInfor.DataDTO userInfor = user.getData();
                            tv_nickname.setText(userInfor.getNikeName());
                            tv_phone.setText(userInfor.getPhone());
                            tv_sex.setText(userInfor.getSex());
                            tv_email.setText(userInfor.getEmail());

                            if(!TextUtils.isEmpty(userInfor.getAddress()))
                            {
                                tv_address.setText(userInfor.getAddress());
                            }

                            tv_lan.setText(userInfor.getLang());
                            tv_country.setText(userInfor.getNationality());
                            tv_timezone.setText(userInfor.getTimezone());

                            String headImage = userInfor.getHeadimg();
                          //  System.out.println("headImage--:"+headImage);

                            int widthPic = DensityUtils.dp2px(getActivity(),50);

                            if(!TextUtils.isEmpty(headImage))
                            {
                                Glide.with(getActivity())
                                        .load(headImage)
                                        .placeholder(R.mipmap.user_head)
                                        .error(R.mipmap.user_head)
                                        .override(widthPic, widthPic)
                                        .centerCrop()
                                        .into(civ_head);
                            }

                        }
                    }

                    @Override
                    public void onFail(Throwable e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    private static final int PAGE_SIZE = 10;
    int  page = 0;
    private void refreshData() {
    }
    private void createChangePasswordDialog()
    {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.update_user_password_dialog, null);
        final Dialog mDialog = new Dialog(getActivity(), R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        EditText et_new = (EditText) mDialog.findViewById(R.id.edit_new_pass);
        EditText et_old = (EditText) mDialog.findViewById(R.id.edit_old_pass);
        Button bt_sure = (Button) mDialog.findViewById(R.id.bt_sure);
        Button bt_cancel = (Button) mDialog.findViewById(R.id.bt_cancel);
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
                 String newPass =   MyUtils.getEditTextContent(et_new);
                 String oldPass =   MyUtils.getEditTextContent(et_old);
                if(TextUtils.isEmpty(newPass)|| TextUtils.isEmpty(oldPass))
                {
                    ToastUtils.showShort(getActivity(),"Password cannot be empty");
                    return;
                }

                UserServiceFactory.getInstance().getService(IUserService.class).changeUserPassword(newPass, oldPass, new IHttpCallBack() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject obj =    new JSONObject(result);
                           if(obj.getInt("code")==200)
                           {
                               ToastUtils.showShort(getActivity(),"Password changed successfully");
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

    private void createSexDialog()
    {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.update_sex_dialog, null);
        final Dialog mDialog = new Dialog(getActivity(), R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        Button bt_nan = (Button) mDialog.findViewById(R.id.bt_nan);
        Button bt_nv = (Button) mDialog.findViewById(R.id.bt_nv);
        Button bt_baomi = (Button) mDialog.findViewById(R.id.bt_baomi);
        Button bt_cancel = (Button) mDialog.findViewById(R.id.bt_cancel);
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
        bt_nan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                updateSex(0);
            }
        });
        bt_nv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                updateSex(1);
            }
        });

        bt_baomi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                updateSex(2);
            }
        });

        mDialog.show();
    }

    private void createUpdateDialog(int type)
    {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.update_user_dialog, null);
        final Dialog mDialog = new Dialog(getActivity(), R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);

        EditText edit_content = (EditText) mDialog.findViewById(R.id.edit_content);
        Button bt_sure = (Button) mDialog.findViewById(R.id.bt_sure);
        Button bt_cancel = (Button) mDialog.findViewById(R.id.bt_cancel);
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });

        bt_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = MyUtils.getEditTextContent(edit_content);
                System.out.println("content-:"+content);
                if(TextUtils.isEmpty(content))
                {
                    mDialog.dismiss();
                }
                else
                {
                    startLoading();
                    if(type==1)
                    {
                        UserServiceFactory.getInstance().getService(IUserService.class).updateUserNickName(content, new IHttpCallBack() {
                            @Override
                            public void onSuccess(String result) {
                                mDialog.dismiss();
                                queryUserInfor();

                            }
                            @Override
                            public void onFail(Throwable e) {
                            }
                        });
                    }
                   else if(type==2)
                    {
                        UserServiceFactory.getInstance().getService(IUserService.class).updateUserAddress(content, new IHttpCallBack() {
                            @Override
                            public void onSuccess(String result) {
                                mDialog.dismiss();
                                queryUserInfor();
                            }
                            @Override
                            public void onFail(Throwable e) {
                            }
                        });
                    }


                }
            }
        });

        mDialog.show();

    }

    private void updateSex(int type)
    {
        UserServiceFactory.getInstance().getService(IUserService.class).updateUserSex(type, new IHttpCallBack() {
        @Override
        public void onSuccess(String result) {

            queryUserInfor();
        }
        @Override
        public void onFail(Throwable e) {
        }
    });

    }
    @OnClick({R.id.ll_nickname,R.id.ll_address,R.id.ll_sex,R.id.ll_lan,
            R.id.ll_country,R.id.ll_timezone,R.id.bt_logout,
            R.id.ll_change_password,R.id.ll_reset_password,R.id.ll_delete_user,
            R.id.ll_change_phone,R.id.civ_head
    })
    public void onViewClick(View view) {
        Intent intent = null;
        AlertDialog.Builder builder = null;
        switch (view.getId()) {

//            case R.id.civ_head:
//                RxPermissions rxPermissions = new RxPermissions(this);
//                rxPermissions
//                        .request(  Manifest.permission.CAMERA,
//                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                                Manifest.permission.READ_EXTERNAL_STORAGE
//                        )
//                        .subscribe(new Consumer<Boolean>() {
//                                       @Override
//                                       public void accept(Boolean grant) throws Exception {
//                                           if (grant) {
//                                               Intent intent = new Intent(getActivity(), UpdateImageActivity.class);
//                                               startActivityForResult(intent,0);
//                                           }
//                                       }
//                                   }
//                        );
//
//                break;

            case R.id.ll_change_phone:

                if(TextUtils.isEmpty(user.getData().getEmail()))
                {
                    startActivity(new Intent(getActivity(), UpdateUserPhoneActivity.class));
                }

              break;

            case R.id.ll_delete_user:
                builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(false);
                builder.setTitle("Confirm Deletion of User?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                UserServiceFactory.getInstance().getService(IUserService.class).deleteUser(1,new IHttpCallBack() {
                                    @Override
                                    public void onSuccess(String result) {
                                        try {
                                            JSONObject  obj = new JSONObject(result);
                                            if (obj.getInt("code") == 200) {
                                                UserServiceFactory.getInstance().getService(IUserService.class).clearToken();
                                                getActivity().finish();
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
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                }).show();
                break;

            case R.id.ll_reset_password:

                if(TextUtils.isEmpty(user.getData().getEmail()))
                {
                    startActivity(new Intent(getActivity(), ResetPasswordByPhoneActivity.class));
                }
                else
                {
                    startActivity(new Intent(getActivity(), ResetPasswordByEmailActivity.class));
                }

                break;

            case R.id.ll_change_password:
                createChangePasswordDialog();
               break;

            case R.id.bt_logout:
                 builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(false);
                builder.setTitle("Confirm Logout?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                UserServiceFactory.getInstance().getService(IUserService.class).userLogout(new IHttpCallBack() {
                                    @Override
                                    public void onSuccess(String result) {
                                        UserInfor userInfor = new Gson().fromJson(result, UserInfor.class);
//                                        if(userInfor.getCode()==200)
//                                        {
//                                            UserServiceFactory.getInstance().getService(IUserService.class).clearToken();
//                                            getActivity().finish();
//                                        }
                                        UserServiceFactory.getInstance().getService(IUserService.class).clearToken();
                                        new Thread(() -> {
                                            IotChannelController.getInstance().closeChannelAll();
                                        }).start();
                                        Intent intent1 = new Intent(getActivity(), LoginActivity.class);
                                        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent1);
                                    }
                                    @Override
                                    public void onFail(Throwable e) {
                                        UserServiceFactory.getInstance().getService(IUserService.class).clearToken();
                                        new Thread(() -> {
                                            IotChannelController.getInstance().closeChannelAll();
                                        }).start();
                                        Intent intent1 = new Intent(getActivity(), LoginActivity.class);
                                        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent1);
                                    }
                                });
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                }).show();

                break;

            case R.id.ll_nickname:

                createUpdateDialog(1);
                break;

            case R.id.ll_address:

                createUpdateDialog(2);
                break;

            case R.id.ll_sex:
                createSexDialog();
                break;

            case R.id.ll_lan:
                intent = new Intent(getActivity(), UpdateUserActivity.class);
                intent.putExtra("type",1);
                startActivityForResult(intent,0);
                break;

            case R.id.ll_country:
                intent = new Intent(getActivity(), UpdateUserActivity.class);
                intent.putExtra("type",2);
                startActivityForResult(intent,0);

                break;
            case R.id.ll_timezone:
                intent = new Intent(getActivity(), UpdateUserActivity.class);
                intent.putExtra("type",3);
                startActivityForResult(intent,0);

                break;

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==1111)
        {
            // queryUserInfor();
        }
          LanVO lanVO = null;
          if(resultCode==2222)
          {
              int type = data.getIntExtra("type",-1);
              switch(type)
              {
                  case 1:
                      lanVO = (LanVO) data.getSerializableExtra("bean");
                      UserServiceFactory.getInstance().getService(IUserService.class).updateUserLanguage(lanVO.getId(), new IHttpCallBack() {
                          @Override
                          public void onSuccess(String result) {
                              queryUserInfor();
                          }
                          @Override
                          public void onFail(Throwable e) {

                          }
                      });
                      break;
                  case 2:
                      lanVO = (LanVO) data.getSerializableExtra("bean");
                      UserServiceFactory.getInstance().getService(IUserService.class).updateUserNationality(lanVO.getId(), new IHttpCallBack() {
                          @Override
                          public void onSuccess(String result) {
                              queryUserInfor();
                          }
                          @Override
                          public void onFail(Throwable e) {

                          }
                      });
                      break;
                  case 3:
                      lanVO = (LanVO) data.getSerializableExtra("bean");
                      UserServiceFactory.getInstance().getService(IUserService.class).updateUserTimezone(lanVO.getId(), new IHttpCallBack() {
                          @Override
                          public void onSuccess(String result) {
                              queryUserInfor();
                          }
                          @Override
                          public void onFail(Throwable e) {

                          }
                      });

                      break;
              }

          }

        if (requestCode == REQUEST_CODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager())
            {
                System.out.println("have permission isExternalStorageManager");
            }
            else
            {
                System.out.println("no permission");
            }
        }

    }
}

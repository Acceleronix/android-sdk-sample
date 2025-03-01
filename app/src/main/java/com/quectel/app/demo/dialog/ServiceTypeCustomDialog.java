package com.quectel.app.demo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.quectel.app.demo.R;
import com.quectel.app.demo.databinding.QuecServiceTypeCustomDialogBinding;
import com.quectel.app.demo.utils.ToastUtils;
import com.quectel.basic.common.utils.QuecFileIOUtil;
import com.quectel.sdk.iot.bean.QuecPublicConfigBean;

import java.io.File;


public class ServiceTypeCustomDialog extends Dialog {

    private Context context;
    private QuecServiceTypeCustomDialogBinding binding;
    private OnServiceTypeCustomConfirmClickListener onConfirmClickListener;


    public ServiceTypeCustomDialog(@NonNull Context context) {
        this(context, R.style.quec_smart_config_BottomDialog);
        this.context = context;
    }

    public ServiceTypeCustomDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected ServiceTypeCustomDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setWindowAnimations(R.style.quec_smart_config_BottomAnim);

        binding = QuecServiceTypeCustomDialogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initLayout();
        initEvent();
    }


    private void initLayout() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = wm.getDefaultDisplay().getWidth();
        getWindow().setAttributes(params);
    }

    public void setOnConfirmClickListener(OnServiceTypeCustomConfirmClickListener confirmClickListener) {
        if (confirmClickListener != null) {
            this.onConfirmClickListener = confirmClickListener;
        }
    }

    private void initEvent() {

        binding.btnReadJson.setOnClickListener(view -> {
            String downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            String jsonPath = downloadPath + "/cloud_config.jpg";
            File file = new File(jsonPath);
            if (!file.exists()){
                ToastUtils.showShort(context,"File：" + jsonPath + " not exist");
                return;
            }

            String jsonContent = QuecFileIOUtil.readFile2String(file);
            QuecPublicConfigBean configBean = new Gson().fromJson(jsonContent, QuecPublicConfigBean.class);
            binding.edtUserDomain.setText(configBean.getUserDomain());
            binding.edtUserDomainSecret.setText(configBean.getUserDomainSecret());
            binding.edtBaseUrl.setText(configBean.getBaseUrl());
            binding.edtWebsocketV2Url.setText(configBean.getWebsocketV2Url());
            binding.edtMcc.setText(configBean.getMcc());
            binding.edtTcpAddr.setText(configBean.getTcpAddr());
            binding.edtPskAddr.setText(configBean.getPskAddr());
            binding.edtTlsAddr.setText(configBean.getTlsAddr());
            binding.edtCerAddr.setText(configBean.getCerAddr());
        });


        binding.btnCancel.setOnClickListener(view -> {
            if (isShowing()) {
                cancel();
            }
        });


        binding.btnSure.setOnClickListener(view -> {
            cancel();
            QuecPublicConfigBean bean = new QuecPublicConfigBean(
                    binding.edtUserDomain.getText().toString(),
                    binding.edtUserDomainSecret.getText().toString(),
                    binding.edtBaseUrl.getText().toString(),
                    binding.edtWebsocketV2Url.getText().toString(),
                    binding.edtMcc.getText().toString(),
                    binding.edtTcpAddr.getText().toString(),
                    binding.edtPskAddr.getText().toString(),
                    binding.edtTlsAddr.getText().toString(),
                    binding.edtCerAddr.getText().toString()
            );
            if (onConfirmClickListener != null) {
                onConfirmClickListener.onServiceTypeCustomConfirm(bean);
            }
        });
    }


    public interface OnServiceTypeCustomConfirmClickListener {
        void onServiceTypeCustomConfirm(QuecPublicConfigBean bean);
    }

}
